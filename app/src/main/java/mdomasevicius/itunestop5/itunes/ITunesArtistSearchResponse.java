package mdomasevicius.itunestop5.itunes;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

import static com.fasterxml.jackson.annotation.Nulls.SKIP;

public class ITunesArtistSearchResponse {

    @JsonSetter(nulls = SKIP)
    public List<ITunesArtist> results = List.of();

    public static class ITunesArtist {
        public Long artistId;
        public String artistName;
    }
}
