package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    public MovieResponse retrieveMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found."));
        return new MovieResponse(movie.getId(),
                movie.getTitle(),movie.getReleaseYear(), movie.getRating(), movie.getPictureUrl(), movie.getLikeCount());
    }

    public MovieResponseWithDetails retrieveMovieWithDetails(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found."));
        return new MovieResponseWithDetails(movie.getId(), movie.getTitle(),
                movie.getDirector(), movie.getReviews(), movie.getReleaseYear(),
                movie.getRating(), movie.getPictureUrl(), movie.getLikeCount());
    }

    public List<MovieResponse> retrieveMostLikedMovies() {
        List<Movie> mostLikedMovies = movieRepository.findAll(Sort.by(Sort.Direction.DESC, "likeCount"));
        return mostLikedMovies.stream().map((m) -> new MovieResponse(m.getId(), m.getTitle(), m.getReleaseYear(), m.getRating(),m.getPictureUrl(), m.getLikeCount())).toList();
    }
    
    public List<MovieResponse> retrieveNewMovies() {
        List<Movie> newMovies = movieRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return newMovies.stream().map((m) -> new MovieResponse(m.getId(), m.getTitle(), m.getReleaseYear(), m.getRating(),m.getPictureUrl(), m.getLikeCount())).toList();
    }

    public Set<MovieResponse> retrieveRecommendedMovies(Principal principal) {
        return null;
//        return new MovieResponse();
    }
    public Set<MovieResponse> retrieveAllMovies(String category) {
        return null;
//        return new MovieResponse();
    }

}
