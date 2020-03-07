package mdomasevicius.itunestop5.artist;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static mdomasevicius.itunestop5.itunes.ITunesResponse.ITunesResult;
import static mdomasevicius.itunestop5.itunes.ITunesResponse.WrapperType.COLLECTION;

class Album {

    public String country;
    public String artistUrl;
    public String artworkUrl;
    public String releaseDate;
    public String genre;

    public static List<Album> albums(List<ITunesResult> results) {
        return results.stream()
            .filter(r -> r.wrapperType == COLLECTION)
            .map(Album::album)
            .collect(toList());
    }

    public static Album album(ITunesResult result) {
        var album = new Album();
        album.country = requireNonNull(result.property("country"), "country can not be null");
        album.artistUrl = requireNonNull(result.property("artistViewUrl"), "artistViewUrl can not be null");
        album.artworkUrl = requireNonNull(result.property("artworkUrl100"), "artworkUrl100 can not be null");
        album.releaseDate = requireNonNull(result.property("releaseDate"), "releaseDate can not be null");
        album.genre = requireNonNull(result.property("primaryGenreName"), "primaryGenreName can not be null");
        return album;
    }
}
