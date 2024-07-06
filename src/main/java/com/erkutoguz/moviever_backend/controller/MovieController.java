package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.Review;
import com.erkutoguz.moviever_backend.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{movieId}")
    public <T> T retrieveMovie(@RequestParam(name = "with-details", defaultValue = "false") Boolean withDetails,
                                       @PathVariable Long movieId) {
        if (!withDetails) {
            return (T) movieService.retrieveMovie(movieId);
        }
        return (T) movieService.retrieveMovieWithDetails(movieId);
    }

    @GetMapping("/most-liked-movies")
    public List<MovieResponse> retrieveMostLikedMovies() {
        return movieService.retrieveMostLikedMovies();
    }

    @GetMapping("/new-movies")
    public List<MovieResponse> retrieveNewMovies() {
        return movieService.retrieveNewMovies();
    }

    @GetMapping("/recommended")
    public Set<MovieResponse> retrieveNewMovies(Principal principal) {
        return movieService.retrieveRecommendedMovies(principal);
    }

    @GetMapping
    public Set<MovieResponse> retrieveAllMovies(@RequestParam(name = "category", defaultValue = "all") String category) {
        return movieService.retrieveAllMovies(category);
    }







}
