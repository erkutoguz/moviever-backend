package com.erkutoguz.moviever_backend.service;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
import com.erkutoguz.moviever_backend.dto.response.ReviewResponse;
import com.erkutoguz.moviever_backend.exception.DuplicateResourceException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.*;
import com.erkutoguz.moviever_backend.repository.*;
import com.erkutoguz.moviever_backend.util.DetailedMovieMapper;
import com.erkutoguz.moviever_backend.util.MovieDocumentMapper;
import com.erkutoguz.moviever_backend.util.MovieMapper;
import com.erkutoguz.moviever_backend.util.SortReviewResponseByLikeCount;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final WatchlistRepository watchlistRepository;
    private final ESProducer esProducer;
    private final DropboxService dropboxService;
    private final ReviewRepository reviewRepository;
    public MovieService(MovieRepository movieRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        WatchlistRepository watchlistRepository,
                        ESProducer esProducer,
                        DropboxService dropboxService, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.watchlistRepository = watchlistRepository;
        this.esProducer = esProducer;
        this.dropboxService = dropboxService;
        this.reviewRepository = reviewRepository;
    }

    public MovieResponse retrieveMovie(Long movieId, String username) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        return MovieMapper.map(movie);
    }

    public Map<String, Object> retrieveMovieReviews(Long movieId, int page, int size) {
        if(!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByMovieId(movieId, pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("reviews", SortReviewResponseByLikeCount.sortByLike(reviewMapper(reviewPage.getContent())));
        map.put("totalItems", reviewPage.getTotalElements());
        map.put("totalPages", reviewPage.getTotalPages());
        return map;
    }

    private List<ReviewResponse> reviewMapper(List<Review> reviews)  {
        return reviews.stream().map(r-> new ReviewResponse(r.getId(), r.getComment(),r.getCreatedAt(),r.getUpdatedAt()
                ,r.getUser().getUsername(),r.getUser().getPictureUrl() , r.getLikeCount())).toList();
    }

    @CacheEvict(value = "mostViewedMovies", allEntries = true)
    public MovieResponseWithDetails retrieveMovieWithDetails(Long movieId, String username) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        movie.addView();
        movieRepository.save(movie);
        boolean isUserLiked = movie.getLiked().contains(user);
        return DetailedMovieMapper.map(movie, isUserLiked);
    }

    @Cacheable(value = "mostViewedMovies", key = "#root.methodName + '-' + #pageNumber + '-' + #pageSize",unless = "#result==null")
    public Map<String, Object> retrieveMostViewedMovies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> mostViewedMovies = movieRepository.findAllByOrderByViewCountDesc(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(mostViewedMovies));
        map.put("totalItems", mostViewedMovies.getTotalElements());
        map.put("totalPages", mostViewedMovies.getTotalPages());
        return map;
    }

    @Cacheable(value = "mostLikedMovies", key = "#root.methodName + '-' + #pageNumber + '-' + #pageSize",unless = "#result==null")
    public Map<String, Object> retrieveMostLikedMovies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "likeCount"));
        Page<Movie> mostLikedMovies = movieRepository.findAll(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(mostLikedMovies));
        map.put("totalItems", mostLikedMovies.getTotalElements());
        map.put("totalPages", mostLikedMovies.getTotalPages());
        return map;
    }

    @Cacheable(value = "newMovies", key = "#root.methodName + '-' + #pageNumber + '-' + #pageSize",unless = "#result==null")
    public Map<String, Object> retrieveNewMovies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Movie> newMovies = movieRepository.findAll(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(newMovies));
        map.put("totalItems", newMovies.getTotalElements());
        map.put("totalPages", newMovies.getTotalPages());
        return map;
    }


    @Cacheable(cacheNames = "allMovies", key = "#root.methodName + '-' + #categoryName + '-' + #pageNumber + '-' + #pageSize",
            unless = "#result==null")
    public Map<String, Object> retrieveAllMovies(CategoryType categoryName, int pageNumber, int pageSize) {
        final Page<Movie> movies;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        if(categoryName.equals(CategoryType.ALL)) {
            movies = movieRepository.findAllByOrderByIdDesc(pageable);
        }else {
            movies = movieRepository.findByCategoryNameByOrderByIdDesc(categoryName, pageable);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("movies", MovieMapper.map(movies));
        map.put("totalItems", movies.getTotalElements());
        map.put("totalPages", movies.getTotalPages());
        return map;
    }

    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void likeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));;
        user.likeMovie(movie);
        userRepository.save(user);
        movieRepository.save(movie);
    }

    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void unlikeMovie(Long movieId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        user.unlikeMovie(movie);
        userRepository.save(user);
        movieRepository.save(movie);
    }


    //ADMIN OPS
    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void createMovie(CreateMovieRequest request) throws IOException, DbxException {
        Optional<Movie> movieExists = movieRepository.findByTitleIgnoreCase(request.title());
        if(movieExists.isPresent()) {
            throw new DuplicateResourceException("Movie already exist");
        }
        Movie movie = builtMovie(request);
        String posterUrl = "";
        if(request.poster() != null) {
            posterUrl = dropboxService.uploadImage("moviePoster", request.title(), request.poster());
        }
        movie.setPictureUrl(posterUrl);
        Movie savedMovie = movieRepository.save(movie);
        esProducer.sendMovieDocument(MovieDocumentMapper.map(savedMovie));
    }

    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void createMultipleMovies(List<CreateMovieRequest> request) {
        request.forEach(r -> {
            if(movieRepository.findByTitleIgnoreCase(r.title()).isPresent()) throw new DuplicateResourceException("Movie already exist");
        });
        List<Movie> movies = request.stream().map(this::builtMovie).toList();
        List<Movie> savedMovies = movieRepository.saveAll(movies);
    }

    private Movie builtMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        request.categories().forEach(categoryType -> {
            movie.addCategory(categoryRepository.findByCategoryName(categoryType));
        });
        movie.setDescription(request.description());
        movie.setDirector(request.director());
        movie.setTitle(request.title());
        movie.setRating(request.rating());
        movie.setTrailerUrl(request.trailerUrl());
        movie.setReleaseYear(request.releaseYear());
        return movie;
    }

    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void deleteMovie(Long movieId){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        for(Watchlist watchlist : movie.getWatchlists()) {
            watchlist.removeMovie(movieId);
            watchlistRepository.save(watchlist);
        }
        for(User user : movie.getLiked()) {
            user.unlikeMovie(movie);
            userRepository.save(user);
        }
        esProducer.sendDeleteMovieMessage(movieId);
        movieRepository.delete(movie);
    }

    @CacheEvict(value = {"newMovies","allMovies", "mostLikedMovies", "mostViewedMovies"}, allEntries = true)
    public void updateMovie(Long movieId, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        movie.setPictureUrl(request.pictureUrl());
        movie.setRating(request.rating());
        movie.setDescription(request.description());
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setReleaseYear(request.releaseYear());
        movieRepository.save(movie);
    }
}
