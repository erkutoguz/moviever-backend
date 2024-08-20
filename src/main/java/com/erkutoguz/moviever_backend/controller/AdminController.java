package com.erkutoguz.moviever_backend.controller;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.dto.response.AdminIpAddressesResponse;
import com.erkutoguz.moviever_backend.dto.response.CategoryMovieCountResponse;
import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.service.AdminService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @GetMapping("/category/distribution")
    public List<CategoryMovieCountResponse> retrieveMovieCountForEachCategory() {
        return adminService.retrieveMovieCountForEachCategory();
    }

    @PostMapping("/movies")
    public ResponseEntity<Void> createMovie(@RequestParam("title") String title,
                                             @RequestParam("director") String director,
                                             @RequestParam("releaseYear") int releaseYear,
                                             @RequestParam("trailerUrl") String trailerUrl,
                                             @RequestParam("rating") double rating,
                                             @RequestParam("categories") String categoriesJson,
                                             @RequestPart("poster") MultipartFile poster)
            throws IOException, DbxException {
        Set<CategoryType> categories = new ObjectMapper()
                .readValue(categoriesJson, new TypeReference<Set<CategoryType>>() {});
        CreateMovieRequest request = new CreateMovieRequest(title,director,releaseYear,poster,trailerUrl,rating,categories);
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

    @GetMapping("/users/ip-addresses")
    public List<AdminIpAddressesResponse> retrieveIpAddresses() {
        return adminService.retrieveIpAddresses();
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId,
                                                 @RequestBody UpdateUserDocumentStatusRequest request) {
        adminService.updateUserStatus(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


}
