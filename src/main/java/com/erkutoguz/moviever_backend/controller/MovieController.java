package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.ReviewRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.service.ElasticsearchService;
import com.erkutoguz.moviever_backend.service.MovieService;
import com.erkutoguz.moviever_backend.service.RecommendedMovieService;
import com.erkutoguz.moviever_backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final RecommendedMovieService recommendedMovieService;
    private final ElasticsearchService elasticsearchService;
    public MovieController(MovieService movieService,
                           ReviewService reviewService,
                           RecommendedMovieService recommendedMovieService, ElasticsearchService elasticsearchService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.recommendedMovieService = recommendedMovieService;
        this.elasticsearchService = elasticsearchService;
    }

    @GetMapping("/{movieId}")
    public <T> T retrieveMovie(@RequestParam(name = "with-details", defaultValue = "false") Boolean withDetails,
                                       @PathVariable Long movieId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!withDetails) {
            return (T) movieService.retrieveMovie(movieId, username);
        }

        return (T) movieService.retrieveMovieWithDetails(movieId, username);
    }

    @GetMapping("/search")
    public Map<String, Object> searchMovies(@RequestParam(defaultValue = "ALL",name = "category") CategoryType categoryType,
                                            @RequestParam String q,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "12") int size) {
        if(categoryType.toString().equals("ALL")) {
            return elasticsearchService.searchMoviesByQuery(q, page, size);
        }
        return elasticsearchService.searchMoviesByQueryAndCategory(q, categoryType.toString(), page,size);
    }

    @GetMapping("/most-liked-movies")
    public Map<String, Object> retrieveMostLikedMovies(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "12") int size) {
        return movieService.retrieveMostLikedMovies(page, size);
    }

    @GetMapping("/most-viewed-movies")
    public Map<String, Object> retrieveMostViewedMovies(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "12") int size) {
        return movieService.retrieveMostViewedMovies(page, size);
    }

    @GetMapping("/new-movies")
    public Map<String, Object> retrieveNewMovies(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "12") int size) {
        return movieService.retrieveNewMovies(page, size);
    }

    @GetMapping("/recommended")
    public  List<MovieResponse> retrieveRecommendedMovies() {
        return recommendedMovieService.getRecommendedMovies();
    }

    @GetMapping
    public Map<String, Object> retrieveAllMovies(@RequestParam(name = "category",defaultValue = "ALL") CategoryType category,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "12") int size) {
        return movieService.retrieveAllMovies(category,page,size);
    }

    @GetMapping("/{movieId}/reviews")
    public Map<String, Object> retrieveMovieReviews(@PathVariable Long movieId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "12") int size) {
        return movieService.retrieveMovieReviews(movieId,page,size);
    }

    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<Void> makeReview(@PathVariable Long movieId,
                                           @Valid @RequestBody ReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        reviewService.makeReview(movieId, request, authentication);
       return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long movieId,@PathVariable Long reviewId) {
        reviewService.deleteReview(movieId, reviewId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/{movieId}/like")
    public ResponseEntity<Void> likeMovie(@PathVariable Long movieId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        movieService.likeMovie(movieId, authentication);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{movieId}/like")
    public ResponseEntity<Void> unlikeMovie(@PathVariable Long movieId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        movieService.unlikeMovie(movieId, authentication);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
