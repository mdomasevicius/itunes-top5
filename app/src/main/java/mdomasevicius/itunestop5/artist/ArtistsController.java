package mdomasevicius.itunestop5.artist;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

import static org.springframework.http.ResponseEntity.*;

@RequestMapping("/api/artists")
@RestController
class ArtistsController {

    private final Artists artists;

    ArtistsController(Artists artists) {
        this.artists = artists;
    }

    @GetMapping
    List<Artist> search(@RequestParam String term) {
        return artists.search(term);
    }

    @GetMapping("/favourites")
    List<Artist> listFavourites(@RequestHeader("User-Id") Long userId) {
        return artists.listFavourites(userId);
    }

    @PostMapping("/favourites")
    ResponseEntity<Void> saveToFavourites(
        @RequestHeader("User-Id") Long userId,
        @Valid @RequestBody FavourArtistRequest request
    ) {
        artists.saveToFavourites(userId, request.ids);
        return noContent().build();
    }

    static class FavourArtistRequest {
        @NotEmpty
        public Set<Long> ids;
    }

    // General consensus is unique resource per controller, but since this is
    // a small app - I don't want to create an extra class just for it
    @GetMapping("/{id}/top5albums")
    List<Album> top5Albums(@PathVariable Long id) {
        return artists.top5Albums(id);
    }
}
