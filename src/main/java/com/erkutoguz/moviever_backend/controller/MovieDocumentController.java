package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.service.ESMovieDocumentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieDocumentController {

    private final ESMovieDocumentService movieDocumentService;

    public MovieDocumentController(ESMovieDocumentService movieDocumentService) {
        this.movieDocumentService = movieDocumentService;
    }

    @GetMapping("/search/{partialMovieName}")
    public Map<String, Object> searchMoviesAutoSuggest(@PathVariable String partialMovieName,
                                                       @RequestParam(value = "category", defaultValue = "") String categoryName,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "12") int size)
            throws IOException {
        return movieDocumentService.searchMoviesAutoSuggest(partialMovieName, categoryName, page,size);
    }


}
