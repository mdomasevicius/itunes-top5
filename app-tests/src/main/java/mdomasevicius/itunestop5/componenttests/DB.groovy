package mdomasevicius.itunestop5.componenttests

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class DB {

    static final Connection connection

    static {
        connection = DriverManager.getConnection(
            'jdbc:postgresql://localhost:5401/itunes',
            'itunes_user',
            'itunes_pass'
        )
    }

    static void clear() {
        [
            connection.prepareStatement('DELETE FROM user_favourite_artists;'),
            connection.prepareStatement('DELETE FROM itunes_artists_by_searched_term;'),
            connection.prepareStatement('DELETE FROM itunes_artist_top5_albums;'),
            connection.prepareStatement('DELETE FROM artists;'),
        ].each { it.executeUpdate() }
    }

    static boolean isArtistTermSearchCached(String term) {
        def rs = DB.connection.prepareStatement("SELECT count(*) FROM itunes_artists_by_searched_term WHERE term = '$term'")
            .executeQuery()

        rs.next()
        return rs.getString('count') as Integer
    }

    static boolean isArtistsSaved(Set<Long> artistIds) {
        def rs = DB.connection.prepareStatement("SELECT count(*) FROM artists WHERE id in (${artistIds.join(',')})")
            .executeQuery()

        rs.next()
        return rs.getString('count') as Integer == artistIds.size()
    }

    static boolean isTop5ArtistAlbumsCached(long artistId) {
        def rs = DB.connection.prepareStatement("SELECT count(*) FROM itunes_artist_top5_albums WHERE artist_id = $artistId")
            .executeQuery()

        rs.next()
        return rs.getString('count') as Integer
    }

    static void main(String[] args) {
        DB.connection.prepareStatement("SELECT * FROM itunes_artists_by_searched_term WHERE term = 'abba'").executeQuery()
    }

}
