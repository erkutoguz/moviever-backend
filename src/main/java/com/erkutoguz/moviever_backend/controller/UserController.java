package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.UpdateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.dto.response.LikedReviewsResponse;
import com.erkutoguz.moviever_backend.service.ReviewService;
import com.erkutoguz.moviever_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ReviewService reviewService;
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }


    @GetMapping("/liked-reviews/{movieId}")
    public LikedReviewsResponse retrieveLikedReviewsForMovie(@PathVariable Long movieId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return reviewService.retrieveLikedReviewsForMovieByUser(authentication,movieId);
    }

}
