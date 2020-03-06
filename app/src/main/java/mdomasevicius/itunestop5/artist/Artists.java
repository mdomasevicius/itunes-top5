package mdomasevicius.itunestop5.artist;

import mdomasevicius.itunestop5.common.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
class Artists {

    private final OkHttpClient httpClient;

    Artists(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<Artist> search(String term) {
        try {
            var response = httpClient.newCall(searchAllArtistsRequest(term)).execute();
            return JSON.MAPPER.readValue(response.body().bytes(), ITunesArtistSearchResponse.class).artists();
        } catch (IOException e) {
            throw new RuntimeException(e); // nothing to do
        }
    }

    private static Request searchAllArtistsRequest(String term) {
        return new Request.Builder()
            .url("https://itunes.apple.com/search?entity=allArtist&term=" + term)
            .build();
    }
}
