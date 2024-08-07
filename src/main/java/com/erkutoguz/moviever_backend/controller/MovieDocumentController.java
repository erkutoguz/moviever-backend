package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieDocumentRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.service.ESMovieDocumentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieDocumentController {

    private final ESMovieDocumentService movieDocumentService;

    public MovieDocumentController(ESMovieDocumentService movieDocumentService) {
        this.movieDocumentService = movieDocumentService;
    }

    @PostMapping("/insert-movie")
    public MovieDocumentResponse insertMovieDocument(@RequestBody CreateMovieDocumentRequest request) {
        return movieDocumentService.insertMovieDocument(request);
    }

    @GetMapping("/search/{partialMovieName}")
    public List<MovieDocumentResponse> searchMoviesAutoSuggest(@PathVariable String partialMovieName) throws IOException {
        return movieDocumentService.searchMoviesAutoSuggest(partialMovieName);
    }

}
