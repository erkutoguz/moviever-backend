package com.erkutoguz.moviever_backend.service;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.dto.response.CategoryMovieCountResponse;
import com.erkutoguz.moviever_backend.model.CategoryType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final MovieService movieService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final WatchlistService watchlistService;
    private final CategoryService categoryService;
    public AdminService(MovieService movieService,
                        UserService userService,
                        ReviewService reviewService,
                        WatchlistService watchlistService, CategoryService categoryService) {
        this.movieService = movieService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.watchlistService = watchlistService;
        this.categoryService = categoryService;
    }

    public Map<String, Object> retrieveAllUsers(int page, int size) {
        return userService.retrieveAllUsers(page, size);
    }

    public Map<String, Object> retrieveAllReviews(int page, int size) {
        return reviewService.retrieveAllReviews(page, size);
    }

    public Map<String, Object> retrieveAllMovies(int page, int size) {
        return movieService.retrieveAllMovies(CategoryType.ALL, page, size);
    }

    public Map<String, Object> retrieveAllWatchlists(int page, int size) {
        return watchlistService.retrieveAllWatchlists(page, size);
    }

    public void createMovie(CreateMovieRequest request) throws IOException, DbxException {
        movieService.createMovie(request);
    }

    public void createMultipleMovies(List<CreateMovieRequest> request) {
        movieService.createMultipleMovies(request);
    }

    public void deleteMovie(Long movieId) {
        movieService.deleteMovie(movieId);
    }

    public void updateMovie(Long movieId, UpdateMovieRequest request) {
        movieService.updateMovie(movieId, request);
    }

    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }


    public List<CategoryMovieCountResponse> retrieveMovieCountForEachCategory() {
        return categoryService.retrieveMovieCountForEachCategory();
    }

    public void updateUserStatus(UpdateUserDocumentStatusRequest request) {
        userService.updateUserStatus(request);
    }
}
