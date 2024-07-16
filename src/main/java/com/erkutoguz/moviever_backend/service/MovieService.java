package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
public class MovieService {

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

    public MovieResponseWithDetails retrieveMovieWithDetails(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        return DetailedMovieMapper.map(movie);
    }

    public List<MovieResponse> retrieveMostLikedMovies() {
        List<Movie> mostLikedMovies = movieRepository.findAll(Sort.by(Sort.Direction.DESC, "likeCount"));
        return MovieMapper.map(mostLikedMovies);
    }
    
    public List<MovieResponse> retrieveNewMovies() {
        List<Movie> newMovies = movieRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return MovieMapper.map(newMovies);
    }

    public Set<MovieResponse> retrieveRecommendedMovies(Principal principal) {
        return null;
//        return new MovieResponse();
    }
    public List<MovieResponse> retrieveAllMovies(CategoryType categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName);
        List<Movie> movies = category.equals("all") ?
                movieRepository.findAll() :
                categoryRepository.findByCategoryName(categoryName).getMovies();
        return MovieMapper.map(movies);
    }

    public void likeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));;
        user.likeMovie(movie);
    }
    public void unlikeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        user.unlikeMovie(movie);
    }

    //ADMIN OPS
    public void createMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        request.categories().forEach(categoryType -> {
            movie.addCategory(categoryRepository.findByCategoryName(categoryType));
        });
        movie.setDirector(request.director());
        movie.setTitle(request.title());
        movie.setPictureUrl(request.pictureUrl());
        movie.setRating(request.rating());
        movie.setReleaseYear(request.releaseYear());
        movieRepository.save(movie);
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
