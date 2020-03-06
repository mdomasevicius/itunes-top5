package mdomasevicius.itunestop5.artist;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static mdomasevicius.itunestop5.itunes.ITunesArtistSearchResponse.ITunesArtist;

class Artist {

    public Long id;
    public String name;

    static Artist artist(ITunesArtist iTunesArtist) {
        Artist artist = new Artist();
        artist.id = requireNonNull(iTunesArtist.artistId, "artistId can not be null");
        artist.name = requireNonNull(iTunesArtist.artistName, "artistName can not be null");
        return artist;
    }

    static List<Artist> artists(List<ITunesArtist> iTunesArtists) {
        return iTunesArtists.stream()
            .map(Artist::artist)
            .collect(toList());
    }
}
