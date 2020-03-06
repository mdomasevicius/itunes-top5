package mdomasevicius.itunestop5.artist;

import mdomasevicius.itunestop5.itunes.ITunesApi;
import mdomasevicius.itunestop5.itunes.ITunesArtistSearchResponse;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mdomasevicius.itunestop5.artist.Artist.artists;

@Component
class Artists {

    private final DSLContext db;
    private final ITunesApi iTunesApi;

    Artists(DSLContext db, ITunesApi iTunesApi) {
        this.db = db;
        this.iTunesApi = iTunesApi;
    }

    public List<Artist> search(String term) {
        ITunesArtistSearchResponse response = iTunesApi.searchAllArtists(term);
        return artists(response.results);
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

        ITunesArtistSearchResponse response = iTunesApi.lookupArtists(new HashSet<>(artistIds));
        return artists(response.results);
    }

    private static class ArtistsTable {
        public final static Table<Record> _USER_FAVOURITE_ARTISTS = DSL.table("user_favourite_artists");
        public final static Field<Long> USER_ID = DSL.field("user_id", Long.class);
        public final static Field<Long> ARTIST_ID = DSL.field("artist_id", Long.class);
    }
}
