package com.erkutoguz.moviever_backend.controller;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.LikedReviewsResponse;
import com.erkutoguz.moviever_backend.dto.response.UserDetailsResponse;
import com.erkutoguz.moviever_backend.service.ReviewService;
import com.erkutoguz.moviever_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/sync-with-es")
    public String syncWithEs() {
        return userService.syncWithEs();
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.updateUser(authentication.getName(), request);
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<Void> uploadProfilePicture(@RequestParam("image") MultipartFile multipartFile)
            throws IOException, DbxException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.uploadProfilePicture(authentication.getName(),multipartFile);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/profile/avatar")
    public ResponseEntity<Void> removeProfilePicture() throws IOException, DbxException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.deleteProfilePicture(authentication.getName());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public UserDetailsResponse retrieveProfile() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.retrieveProfile(authentication.getName());
    }

    @GetMapping("/liked-reviews/{movieId}")
    public LikedReviewsResponse retrieveLikedReviewsForMovie(@PathVariable Long movieId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return reviewService.retrieveLikedReviewsForMovieByUser(authentication,movieId);
    }

}
