package mdomasevicius.itunestop5.artist;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.Nulls.SKIP;

class ITunesArtistSearchResponse {

    @JsonSetter(nulls = SKIP)
    public List<ITunesArtist> results = List.of();

    List<Artist> artists() {
        return results.stream()
            .map(ITunesArtist::artist)
            .collect(Collectors.toList());
    }

    static class ITunesArtist {

        public Long artistId;
        public String artistName;

        Artist artist() {
            Artist artist = new Artist();
            artist.id = artistId;
            artist.name = artistName;
            return artist;
        }
    }
}
