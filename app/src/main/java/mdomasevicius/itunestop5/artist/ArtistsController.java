package mdomasevicius.itunestop5.artist;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
