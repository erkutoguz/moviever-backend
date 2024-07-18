package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.service.MovieService;
import com.erkutoguz.moviever_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MovieService movieService;
    private final UserService userService;
    public AdminController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    @PostMapping("/movies")
    public ResponseEntity<Void> createMovie(@RequestBody CreateMovieRequest request) {
        movieService.createMovie(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/movies-multiple")
    public ResponseEntity<Void> createMultipleMovies(@RequestBody List<CreateMovieRequest> request) {
        movieService.createMultipleMovies(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId){
        movieService.deleteMovie(movieId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PatchMapping("/movies/{movieId}")
    public ResponseEntity<Void> updateMovie(@PathVariable Long movieId, @RequestBody UpdateMovieRequest request) {
        movieService.updateMovie(movieId, request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
