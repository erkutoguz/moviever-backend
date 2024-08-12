package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public Map<String, Object> retrieveAllUsers(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "1") int size) {
        return adminService.retrieveAllUsers(page, size);
    }

    @GetMapping("/reviews")
    public Map<String, Object> retrieveAllReviews(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "1") int size) {
        return adminService.retrieveAllReviews(page, size);
    }

    @GetMapping("/movies")
    public Map<String, Object> retrieveAllMovies(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "1") int size) {
        return adminService.retrieveAllMovies(page, size);
    }
    @GetMapping("/watchlists")
    public Map<String, Object> retrieveAllWatchlists(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "1") int size) {
        return adminService.retrieveAllWatchlists(page, size);
    }

    @PostMapping("/movies")
    public ResponseEntity<Void> createMovie(@RequestBody CreateMovieRequest request) {
        adminService.createMovie(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/movies-multiple")
    public ResponseEntity<Void> createMultipleMovies(@RequestBody List<CreateMovieRequest> request) {
        adminService.createMultipleMovies(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId){
        adminService.deleteMovie(movieId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PatchMapping("/movies/{movieId}")
    public ResponseEntity<Void> updateMovie(@PathVariable Long movieId, @RequestBody UpdateMovieRequest request) {
        adminService.updateMovie(movieId, request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
