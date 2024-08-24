package com.erkutoguz.moviever_backend.service;


import com.erkutoguz.moviever_backend.dto.request.RecommendedMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.MovieMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendedMovieService {

    private final RestClient restClient;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    public RecommendedMovieService(RestClient restClient,
                                   UserRepository userRepository,
                                   MovieRepository movieRepository) {
        this.restClient = restClient;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public List<MovieResponse> getRecommendedMovies() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Movie> allMovies = movieRepository.findAll();
        List<RecommendedMovieRequest> allMoviesResponse =  map(allMovies);
        List<Movie> userLikedMovies = user.getLikedMovies();
        List<RecommendedMovieRequest> userLikedMoviesResponse = map(userLikedMovies);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("allMovies", allMoviesResponse);
        requestMap.put("userLikedMovies", userLikedMoviesResponse);

        ResponseEntity<List<List<Object>>> response = restClient.post()
                .uri("http://localhost:5000/recommended-movies")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestMap)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<List<Object>>>() {});

        List<List<Object>> responseBody = response.getBody();

        List<Long> recommendedMovieResponses = responseBody.stream()
                .map(item -> ((Integer) item.getFirst()).longValue()).toList();

        List<MovieResponse> movieResponses = new ArrayList<>();
        for(Long id : recommendedMovieResponses) {
            Movie m = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            movieResponses.add(MovieMapper.map(m));
        }
        return movieResponses;
    }

    private List<RecommendedMovieRequest> map(List<Movie> movies) {
        if(movies.isEmpty()) return null;
        return movies.stream().map(this::map).toList();
    }


    private RecommendedMovieRequest map(Movie movie) {
        if(movie == null) return null;
        List<String> categories = movie.getCategories().stream().map(c -> c.getCategoryName().name()).toList();
        return new RecommendedMovieRequest(movie.getId(), movie.getRating(), movie.getLikeCount(), movie.getViewCount(), categories);
    }

}
