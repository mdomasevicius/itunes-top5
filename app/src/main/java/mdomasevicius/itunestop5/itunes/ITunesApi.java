package mdomasevicius.itunestop5.itunes;

import mdomasevicius.itunestop5.common.GenericBadRequestException;
import mdomasevicius.itunestop5.common.Conversions;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@Component
public class ITunesApi {

    private final static String BASE_URL = "https://itunes.apple.com";
    private final OkHttpClient httpClient;

    public ITunesApi(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ITunesResponse searchAllArtists(String term) {
        try {
            var body = executeCall(iTunesSearchRequest(term)).body();
            return Conversions.readValue(body.bytes(), ITunesResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex); // nothing to do
        }
    }

    public ITunesResponse lookupArtists(Set<Long> artistIds) {
        try {
            var idsString = artistIds.stream().map(String::valueOf).collect(joining(","));
            var response = executeCall(iTunesLookupRequest("?id=" + idsString));
            return Conversions.readValue(response.body().bytes(), ITunesResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Request iTunesSearchRequest(String term) {
        return new Request.Builder()
            .url(BASE_URL + "/search?entity=allArtist&term=" + term)
            .build();
    }



    private Response executeCall(Request request) throws IOException {
        var response = httpClient.newCall(request).execute();
        if (response.code() > 299 || response.code() < 200) {
            throw new GenericBadRequestException("iTunes API returned: " + response.code());
        }
        return response;
    }

    public ITunesResponse top5Albums(Long artistId) {
        try {
            var response = executeCall(iTunesLookupRequest("?id=" + artistId + "&entity=album&limit=5"));
            return Conversions.readValue(response.body().bytes(), ITunesResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Request iTunesLookupRequest(String queryString) {
        return new Request.Builder()
            .url(BASE_URL + "/lookup" + queryString)
            .build();
    }
}
