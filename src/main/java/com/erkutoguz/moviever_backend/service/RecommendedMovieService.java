package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.RecommendedMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.dto.response.RecommendedMovieResponse;
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

import java.util.*;
import java.util.stream.Collectors;

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
        if(allMoviesResponse == null) {
            return new ArrayList<>();
        }
        List<Movie> userLikedMovies = user.getLikedMovies();
        List<RecommendedMovieRequest> userLikedMoviesResponse = map(userLikedMovies);

        List<Movie> userWatchlistMovies = user.getWatchlists().stream()
                .flatMap(w -> w.getMovies().stream()).distinct().collect(Collectors.toList());

        List<RecommendedMovieRequest> userWatchlistMoviesResponse = map(userWatchlistMovies);

        List<RecommendedMovieResponse> recommendedMovies = new ArrayList<>();

        for (RecommendedMovieRequest movie : allMoviesResponse) {
            RecommendedMovieResponse movieScore = new RecommendedMovieResponse(movie.id(),
                    calculateMovieScore(movie, userLikedMoviesResponse, userWatchlistMoviesResponse));
            recommendedMovies.add(movieScore);
        }
        if(recommendedMovies.size() < 12) {
            recommendedMovies = new ArrayList<>(recommendedMovies.subList(0,recommendedMovies.size()));
        }else {
            recommendedMovies = new ArrayList<>(recommendedMovies.subList(0,12));
        }

        recommendedMovies.sort(Comparator.comparingDouble(RecommendedMovieResponse::score).reversed());;

        List<Long> recommendedMovieResponses = recommendedMovies.stream()
                .map(RecommendedMovieResponse::movieId).toList();

        List<MovieResponse> movieResponses = new ArrayList<>();
        for(Long id : recommendedMovieResponses) {
            Movie m = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            movieResponses.add(MovieMapper.map(m));
        }
        return movieResponses;
    }

    private double calculateMovieScore(RecommendedMovieRequest movie,
                                       List<RecommendedMovieRequest> userLikedMovies,
                                       List<RecommendedMovieRequest> userWatchlistMovies) {
        Map<String, Double> weights = new HashMap<>();
        weights.put("sameCategory", 0.3);
        weights.put("rating", 0.2);
        weights.put("likes", 0.4);
        weights.put("view", 0.3);

        double score = 0;

        Map<Long, RecommendedMovieRequest> userLikedMoviesMap = new HashMap<>();
        if(userLikedMovies != null) {
            for(RecommendedMovieRequest m : userLikedMovies) {
                userLikedMoviesMap.put(m.id(), m);
            }
            if(userLikedMoviesMap.containsKey(movie.id())) {
                score++;
            }
            for(RecommendedMovieRequest likedMovie : userLikedMovies) {
                for(String category : likedMovie.categories()) {
                    if(movie.categories().contains(category)) {
                        score += weights.get("sameCategory");
                    }
                }
            }
        }

        Map<Long, RecommendedMovieRequest> userWatchlistMoviesMap = new HashMap<>();
        if(userWatchlistMovies != null){
            for(RecommendedMovieRequest m : userWatchlistMovies) {
                userWatchlistMoviesMap.put(m.id(), m);
            }
            if(userWatchlistMoviesMap.containsKey(movie.id())) {
                score++;
            }

            for(RecommendedMovieRequest likedMovie : userWatchlistMovies) {
                for(String category : likedMovie.categories()) {
                    if(movie.categories().contains(category)) {
                        score += weights.get("sameCategory");
                    }
                }
            }
        }

        score += weights.get("rating") * movie.rating();
        score += weights.get("view") * movie.view();
        score += weights.get("likes") * movie.likes() / 1000;
        return score;
    }

    public List<MovieResponse> getRecommendedMoviesFromPythonServer() {
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
