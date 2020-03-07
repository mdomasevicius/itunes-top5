package mdomasevicius.itunestop5.artist;

import com.fasterxml.jackson.core.type.TypeReference;
import mdomasevicius.itunestop5.itunes.ITunesApi;
import mdomasevicius.itunestop5.itunes.ITunesResponse;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static mdomasevicius.itunestop5.artist.Album.albums;
import static mdomasevicius.itunestop5.artist.Artist.artists;
import static mdomasevicius.itunestop5.common.Conversions.readValue;
import static mdomasevicius.itunestop5.common.Conversions.writeValue;
import static mdomasevicius.itunestop5.itunes.ITunesResponse.ITunesResult;

@Component
class Artists {

    private final DSLContext db;
    private final ITunesApi iTunesApi;

    Artists(DSLContext db, ITunesApi iTunesApi) {
        this.db = db;
        this.iTunesApi = iTunesApi;
    }

    public List<Artist> search(String term) {
        List<ITunesResult> results = searchArtistInDb(term)
            .map(json -> readValue(json.data(), new TypeReference<List<ITunesResult>>() { }))
            .orElseGet(() -> remoteSearchArtistsAndCache(term));

        return artists(results);
    }

    private List<ITunesResult> remoteSearchArtistsAndCache(String term) {
        var results = iTunesApi.searchAllArtists(term).results;

        db.transaction(tx ->
            tx.dsl()
                .insertInto(ITunesArtistsBySearchedTermTable._ITUNES_ARTISTS_BY_SEARCHED_TERM)
                .set(ITunesArtistsBySearchedTermTable.TERM, term)
                .set(ITunesArtistsBySearchedTermTable.UPDATED_ON, Instant.now())
                .set(ITunesArtistsBySearchedTermTable.ARTISTS, JSONB.valueOf(writeValue(results)))
                .onConflict(ITunesArtistsBySearchedTermTable.TERM)
                .doUpdate()
                .set(ITunesArtistsBySearchedTermTable.UPDATED_ON, Instant.now())
                .set(ITunesArtistsBySearchedTermTable.ARTISTS, JSONB.valueOf(writeValue(results)))
                .execute()
        );

        return results;
    }

    private Optional<JSONB> searchArtistInDb(String term) {
        return db.select(ITunesArtistsBySearchedTermTable.ARTISTS)
            .from(ITunesArtistsBySearchedTermTable._ITUNES_ARTISTS_BY_SEARCHED_TERM)
            .where(
                ITunesArtistsBySearchedTermTable.TERM.eq(term),
                // 1 day cache
                ITunesArtistsBySearchedTermTable.UPDATED_ON.gt(Instant.now().minus(of(1, DAYS)))
            )
            .fetchOptionalInto(JSONB.class);
    }

    @Transactional
    public void saveToFavourites(Long userId, Set<Long> artistIds) {
        artistIds.forEach(artistId ->
            db.insertInto(ArtistsTable._USER_FAVOURITE_ARTISTS)
                .set(ArtistsTable.USER_ID, userId)
                .set(ArtistsTable.ARTIST_ID, artistId)
                .onConflict(ArtistsTable.USER_ID, ArtistsTable.ARTIST_ID)
                .doNothing()
                .execute()
        );
    }

    @Transactional(readOnly = true)
    public List<Artist> listFavourites(Long userId) {
        var artistIds = db.selectDistinct(ArtistsTable.ARTIST_ID)
            .from(ArtistsTable._USER_FAVOURITE_ARTISTS)
            .where(ArtistsTable.USER_ID.eq(userId))
            .fetchInto(Long.class);

        ITunesResponse response = iTunesApi.lookupArtists(new HashSet<>(artistIds));
        return artists(response.results);
    }

    public List<Album> top5Albums(long artistId) {
        List<ITunesResult> results = searchAlbumsInDb(artistId)
            .map(json -> readValue(json.data(), new TypeReference<List<ITunesResult>>() {
            }))
            .orElseGet(() -> lookupTop5AlbumsAndCache(artistId));

        return albums(results);
    }

    private List<ITunesResult> lookupTop5AlbumsAndCache(long artistId) {
        ITunesResponse response = iTunesApi.top5Albums(artistId);

        db.transaction(tx ->
            tx.dsl()
                .insertInto(ITunesArtistTop5Albums._ITUNES_ARTIST_TOP5_ALBUMS)
                .set(ITunesArtistTop5Albums.ARTIST_ID, artistId)
                .set(ITunesArtistTop5Albums.UPDATED_ON, Instant.now())
                .set(ITunesArtistTop5Albums.ITUNES_TOP_5_ALBUMS, JSONB.valueOf(writeValue(response.results)))
                .onConflict(ITunesArtistTop5Albums.ARTIST_ID)
                .doUpdate()
                .set(ITunesArtistTop5Albums.ITUNES_TOP_5_ALBUMS, JSONB.valueOf(writeValue(response.results)))
                .execute()
        );

        return response.results;
    }

    private Optional<JSONB> searchAlbumsInDb(long artistId) {
        return db.select(ITunesArtistTop5Albums.ITUNES_TOP_5_ALBUMS)
            .from(ITunesArtistTop5Albums._ITUNES_ARTIST_TOP5_ALBUMS)
            .where(
                ITunesArtistTop5Albums.ARTIST_ID.eq(artistId),
                // 1 day cache
                ITunesArtistTop5Albums.UPDATED_ON.gt(Instant.now().minus(of(1, DAYS)))
            )
            .fetchOptionalInto(JSONB.class);
    }

    private static class ArtistsTable {
        public final static Table<Record> _USER_FAVOURITE_ARTISTS = DSL.table("user_favourite_artists");
        public final static Field<Long> USER_ID = DSL.field("user_id", Long.class);
        public final static Field<Long> ARTIST_ID = DSL.field("artist_id", Long.class);
    }

    private static class ITunesArtistsBySearchedTermTable {
        public final static Table<Record> _ITUNES_ARTISTS_BY_SEARCHED_TERM = DSL.table("itunes_artists_by_searched_term");
        public final static Field<String> TERM = DSL.field("term", String.class);
        public final static Field<Instant> UPDATED_ON = DSL.field("updated_on", Instant.class);
        public final static Field<JSONB> ARTISTS = DSL.field("itunes_artists", JSONB.class);
    }

    private static class ITunesArtistTop5Albums {
        public final static Table<Record> _ITUNES_ARTIST_TOP5_ALBUMS = DSL.table("itunes_artist_top5_albums");
        public final static Field<Long> ARTIST_ID = DSL.field("artist_id", Long.class);
        public final static Field<Instant> UPDATED_ON = DSL.field("updated_on", Instant.class);
        public final static Field<JSONB> ITUNES_TOP_5_ALBUMS = DSL.field("itunes_top_5_albums", JSONB.class);
    }
}
