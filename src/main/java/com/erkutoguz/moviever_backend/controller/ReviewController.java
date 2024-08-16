package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likeMovie(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        reviewService.likeReview(reviewId, authentication);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<Void> unlikeMovie(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        reviewService.unlikeReview(reviewId, authentication);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/sync-with-es")
    public String syncWithEs() {
        return reviewService.syncWithEs();
    }

}
