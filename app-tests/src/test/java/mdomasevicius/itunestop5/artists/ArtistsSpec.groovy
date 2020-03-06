package mdomasevicius.itunestop5.artists


import spock.lang.Shared
import spock.lang.Specification

import static mdomasevicius.itunestop5.componenttests.User.user

class ArtistsSpec extends Specification {

    @Shared
    def user = user()

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
}
