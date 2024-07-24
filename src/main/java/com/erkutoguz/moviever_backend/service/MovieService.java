package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
import com.erkutoguz.moviever_backend.dto.response.ReviewResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Category;
import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.CategoryRepository;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.DetailedMovieMapper;
import com.erkutoguz.moviever_backend.util.MovieMapper;
import com.erkutoguz.moviever_backend.util.ReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    public MovieService(MovieRepository movieRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    public MovieResponse retrieveMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        return MovieMapper.map(movie);
    }
    public List<ReviewResponse> retrieveMovieReviews(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        return ReviewMapper.map(movie.getReviews());
    }

    public MovieResponseWithDetails retrieveMovieWithDetails(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        User user =(User) userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isUserLiked = movie.getLiked().contains(user);
        return DetailedMovieMapper.map(movie, isUserLiked);
    }

    public Map<String, Object> retrieveMostLikedMovies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "likeCount"));
        Page<Movie> mostLikedMovies = movieRepository.findAll(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(mostLikedMovies));
        map.put("totalItems", mostLikedMovies.getTotalElements());
        map.put("totalPages", mostLikedMovies.getTotalPages());
        return map;
    }
    
    public Map<String, Object> retrieveNewMovies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Movie> newMovies = movieRepository.findAll(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(newMovies));
        map.put("totalItems", newMovies.getTotalElements());
        map.put("totalPages", newMovies.getTotalPages());
        return map;
    }

    public Set<MovieResponse> retrieveRecommendedMovies(Principal principal) {
        return null;
//        return new MovieResponse();
    }
    public Map<String, Object> retrieveAllMovies(CategoryType categoryName, int pageNumber, int pageSize) {
        final Page<Movie> movies;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(categoryName.equals(CategoryType.ALL)) {
            movies = movieRepository.findAll(pageable);
        }else {
            movies = movieRepository.findByCategoryName(categoryName, pageable);
        }
//        Category category = categoryRepository.findByCategoryName(categoryName);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//        Page<Movie> movies = (Page<Movie>) (category.equals("ALL") ?
//                        movieRepository.findAll() :
//                        movieRepository.findByCategoryName(categoryName, pageable));

        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(movies));
        map.put("totalItems", movies.getTotalElements());
        map.put("totalPages", movies.getTotalPages());
        return map;
    }

    public void likeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));;
        user.likeMovie(movie);
        userRepository.save(user);
        movieRepository.save(movie);
    }
    public void unlikeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        user.unlikeMovie(movie);
        userRepository.save(user);
        movieRepository.save(movie);
    }

    private Movie builtMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        request.categories().forEach(categoryType -> {
            movie.addCategory(categoryRepository.findByCategoryName(categoryType));
        });
        movie.setDirector(request.director());
        movie.setTitle(request.title());
        movie.setPictureUrl(request.pictureUrl());
        movie.setRating(request.rating());
        movie.setTrailerUrl(request.trailerUrl());
        movie.setReleaseYear(request.releaseYear());
        return movie;
    }

    //ADMIN OPS
    public void createMovie(CreateMovieRequest request) {
        movieRepository.save(builtMovie(request));
    }

    public void createMultipleMovies(List<CreateMovieRequest> request) {
        List<Movie> movies = request.stream().map(this::builtMovie).toList();
        movieRepository.saveAll(movies);
    }

    public void deleteMovie(Long movieId){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        movieRepository.delete(movie);
    }

    public void updateMovie(Long movieId, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        request.updateMovie(movie);
        movieRepository.save(movie);
    }
}
