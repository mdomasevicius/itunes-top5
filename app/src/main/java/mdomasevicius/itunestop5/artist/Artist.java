package mdomasevicius.itunestop5.artist;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static mdomasevicius.itunestop5.itunes.ITunesResponse.ITunesResult;
import static mdomasevicius.itunestop5.itunes.ITunesResponse.WrapperType.ARTIST;

class Artist {

    public Long id;
    public String name;

    static Artist artist(ITunesResult result) {
        Artist artist = new Artist();
        artist.id = requireNonNull(Long.valueOf(result.property("artistId")), "artistId can not be null");
        artist.name = requireNonNull(result.property("artistName"), "artistName can not be null");
        return artist;
    }

    static List<Artist> artists(List<ITunesResult> iTunesArtists) {
        return iTunesArtists.stream()
            .filter(r -> r.wrapperType == ARTIST)
            .map(Artist::artist)
            .collect(toList());
    }
}
