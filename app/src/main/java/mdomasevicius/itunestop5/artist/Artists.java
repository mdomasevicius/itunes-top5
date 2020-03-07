package mdomasevicius.itunestop5.artist;

import com.fasterxml.jackson.core.type.TypeReference;
import mdomasevicius.itunestop5.common.Conversions;
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
import static mdomasevicius.itunestop5.artist.Artist.artists;
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
        List<ITunesResult> results = findInDb(term)
            .map(json -> Conversions.readValue(json.data(), new TypeReference<List<ITunesResult>>() {
            }))
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
                .set(ITunesArtistsBySearchedTermTable.ARTISTS, JSONB.valueOf(Conversions.writeValue(results)))
                .onConflict(ITunesArtistsBySearchedTermTable.TERM)
                .doUpdate()
                .set(ITunesArtistsBySearchedTermTable.UPDATED_ON, Instant.now())
                .set(ITunesArtistsBySearchedTermTable.ARTISTS, JSONB.valueOf(Conversions.writeValue(results)))
                .execute()
        );

        return results;
    }

    private Optional<JSONB> findInDb(String term) {
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
}
