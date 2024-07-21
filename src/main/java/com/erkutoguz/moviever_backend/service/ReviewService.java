package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.ReviewRequest;
import com.erkutoguz.moviever_backend.dto.response.LikedReviewsResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.Review;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.ReviewRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.LikeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ReviewService {

    private  final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public void makeReview(Long movieId, ReviewRequest request, Authentication authentication) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        User user = (User) userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = new Review();
        review.setComment(request.comment());
        review.setMovie(movie);
        review.setUser(user);
        movie.makeReview(review);
        user.makeReview(review);
        reviewRepository.save(review);
    }

    public void deleteReview(Long movieId, Long reviewId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        movie.deleteReview(review);
        reviewRepository.delete(review);
    }

    public LikedReviewsResponse retrieveLikedReviewsByUser(Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return LikeMapper.map(user.getLikedReviews());
    }

    public void likeReview(Long reviewId, Authentication authentication) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));;
        user.likeReview(review);
        userRepository.save(user);
        reviewRepository.save(review);
    }

    public void unlikeReview(Long reviewId, Authentication authentication) {
        log.info("auth is {}", authentication.getName());

        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));;
        user.unlikeReview(review);
        userRepository.save(user);
        reviewRepository.save(review);
    }
}