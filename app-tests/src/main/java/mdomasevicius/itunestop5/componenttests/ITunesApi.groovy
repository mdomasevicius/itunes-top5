package mdomasevicius.itunestop5.componenttests

class ITunesApi {

    private final static String BASE_URI = 'https://itunes.apple.com'
    private final HttpClient httpClient = new HttpClient(BASE_URI)

    FluentResponse searchArtistsByTerm(String term) {
        def response = httpClient.get("/search?entity=allArtist&term=$term")
        assert response.status == 200
        return response
    }

    FluentResponse top5albums(long artistId) {
        def response = httpClient.get("/lookup?id=$artistId&entity=album&limit=5")
        assert response.status == 200
        return response
    }
}
