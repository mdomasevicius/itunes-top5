package mdomasevicius.itunestop5.componenttests

import groovy.transform.CompileStatic

import static java.lang.Long.*
import static java.util.concurrent.ThreadLocalRandom.*

@CompileStatic
class User {

    long userId

    @Delegate
    ArtistActions artistActions

    static User user() {
        def userId = current().nextLong(MAX_VALUE)
        def http = new HttpClient()
        return new User(
            userId: userId,
            artistActions: new ArtistActions(http),
        )
    }
}
