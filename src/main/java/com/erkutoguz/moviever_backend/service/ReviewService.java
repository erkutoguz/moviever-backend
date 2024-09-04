package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.ReviewRequest;
import com.erkutoguz.moviever_backend.dto.response.LikedReviewsResponse;
import com.erkutoguz.moviever_backend.exception.AccessDeniedException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.Review;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.ReviewRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.LikeMapper;
import com.erkutoguz.moviever_backend.util.ReviewDocumentMapper;
import com.erkutoguz.moviever_backend.util.ReviewMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ESProducer esProducer;
    public ReviewService(ReviewRepository reviewRepository,
                         MovieRepository movieRepository,
                         UserRepository userRepository,
                         ESProducer esProducer) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.esProducer = esProducer;
    }

    public Map<String, Object> retrieveAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        final Page<Review> reviews = reviewRepository.findAllByOrderByIdDesc(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("reviews", ReviewMapper.map(reviews.getContent()));
        map.put("totalItems", reviews.getTotalElements());
        map.put("totalPages", reviews.getTotalPages());
        return map;
    }

    public void makeReview(Long movieId, ReviewRequest request, Authentication authentication) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = new Review();
        review.setComment(request.comment());
        review.setMovie(movie);
        review.setUser(user);
        movie.makeReview(review);
        user.makeReview(review);
        reviewRepository.save(review);
        esProducer.sendReviewDocument(ReviewDocumentMapper.map(review));
    }


    public void deleteReview(Long movieId, Long reviewId) {
        if(!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) throw new AccessDeniedException("Unauthenticated user");
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        movie.deleteReview(review);
        esProducer.sendDeleteReviewMessage(review.getId());
        reviewRepository.delete(review);
    }

    public LikedReviewsResponse retrieveLikedReviewsForMovieByUser(Authentication authentication, Long movieId) {
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Predicate<Review> predicate = r -> r.getMovie().getId() == movieId;
        return LikeMapper.map(user.getLikedReviews().stream().filter(predicate).toList());
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
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));;
        user.unlikeReview(review);
        userRepository.save(user);
        reviewRepository.save(review);
    }

}