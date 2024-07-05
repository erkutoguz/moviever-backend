package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.service.ReviewService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


}
