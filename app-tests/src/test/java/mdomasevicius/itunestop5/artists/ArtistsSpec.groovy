package mdomasevicius.itunestop5.artists

import mdomasevicius.itunestop5.componenttests.ITunesApi
import spock.lang.Shared
import spock.lang.Specification

import static mdomasevicius.itunestop5.componenttests.User.newUser

class ArtistsSpec extends Specification {

    @Shared
    def iTunesApi = new ITunesApi()

    @Shared
    def user = newUser()

    def 'search for artists'() {
        given:
            def iTunesArtists = iTunesApi.searchArtistsByTerm('abba').body.results
        expect:
            with(user.searchArtists('abba')) { response ->
                response.status == 200
                response.body

                iTunesArtists*.artistName as Set == response.body*.name as Set
                iTunesArtists*.artistId as Set == response.body*.id as Set

                with(response.body.first()) {
                    id
                    name
                }
            }
    }

    def 'save artists to favourites'() {
        given:
            def artists = findArtists('scooter')
        expect:
            with(user.saveArtistToFavourites([ids: artists*.id])) { response ->
                response.status == 204
            }

            with(user.listFavouriteArtists()) { response ->
                response.status == 200
                artists*.id as Set == response.body*.id as Set
                artists*.name as Set == response.body*.name as Set
            }

        and:
            !newUser().listFavouriteArtists().body
    }

    def 'list top 5 artist albums'() {
        given:
            def artistId = findArtists('beach boys').first().id as long
            def top5artistAlbums = iTunesApi.top5albums(artistId).body.results.findAll { it.wrapperType == 'collection' }
        expect:

            with(user.listTop5Albums(artistId)) { response ->
                response.status == 200

                top5artistAlbums
                top5artistAlbums*.country as Set == response.body*.country as Set
                top5artistAlbums*.releaseDate as Set == response.body*.releaseDate as Set
                top5artistAlbums*.artistViewUrl as Set == response.body*.artistUrl as Set
                top5artistAlbums*.artworkUrl100 as Set == response.body*.artworkUrl as Set
                top5artistAlbums*.primaryGenreName as Set == response.body*.genre as Set

                response.body.size() <= 5
                response.body.every {
                    it.country
                    it.artistUrl
                    it.artworkUrl
                    it.releaseDate
                    it.genre
                }
            }
    }

    Object findArtists(String term) {
        def response = user.searchArtists(term)
        assert response.status == 200 && response.body
        return response.body
    }
}
