package mdomasevicius.itunestop5.artists

import spock.lang.Shared
import spock.lang.Specification

import static mdomasevicius.itunestop5.componenttests.User.newUser

class ArtistsSpec extends Specification {

    @Shared
    def user = newUser()

    def 'search for artists'() {
        expect:
            with(user.searchArtists('abba')) { response ->
                response.status == 200
                response.body

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

    List<Map> findArtists(String term) {
        def response = user.searchArtists(term)
        assert response.status == 200 && response.body
        return response.body
    }
}
