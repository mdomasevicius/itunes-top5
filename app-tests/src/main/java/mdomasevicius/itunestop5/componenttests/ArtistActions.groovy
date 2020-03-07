package mdomasevicius.itunestop5.componenttests

import groovy.transform.CompileStatic

@CompileStatic
class ArtistActions {

    private final HttpClient http

    ArtistActions(HttpClient http) {
        this.http = http
    }

    FluentResponse searchArtists(String term) {
        return http.get('/api/artists', [term: term])
    }

    FluentResponse saveArtistToFavourites(Map payload) {
        return http.post('/api/artists/favourites', payload)
    }

    FluentResponse listFavouriteArtists() {
        return http.get('/api/artists/favourites')
    }

    FluentResponse listTop5Albums(Long artistId) {
        return http.get("/api/artists/$artistId/top5albums")
    }
}
