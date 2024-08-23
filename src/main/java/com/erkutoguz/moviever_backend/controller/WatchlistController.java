package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.CreateWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.RenameWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.WatchlistMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponse;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponsePreview;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponseWithMovies;
import com.erkutoguz.moviever_backend.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<WatchlistResponse> retrieveUserWatchlists(Principal principal) {
        return watchlistService.retrieveUserWatchlists(principal);
    }

    @GetMapping("/{watchlistId}/movies")
    public WatchlistResponseWithMovies retrieveWatchlist(@PathVariable Long watchlistId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "12") int size){
        return watchlistService.retrieveWatchlist(watchlistId, page, size);
    }


    @GetMapping("/preview")
    public List<WatchlistResponsePreview> retrieveWatchlistPreview() {
        return watchlistService.retrieveWatchlistsPreview(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PatchMapping("/{watchlistId}")
    public ResponseEntity<Void> renameWatchlist(@Valid @RequestBody RenameWatchlistRequest request,
                                                @PathVariable Long watchlistId) {
        watchlistService.renameWatchlist(watchlistId, request);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Void> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request) {
        watchlistService.createWatchlist(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/{watchlistId}")
    public ResponseEntity<Void> addMovieToWatchlist(@PathVariable Long watchlistId,
                                                    @Valid @RequestBody WatchlistMovieRequest request) {
        watchlistService.addMovieToWatchlist(watchlistId, request);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(watchlistId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}/movies/{movieId}")
    public ResponseEntity<Void> deleteMovieFromWatchlist(@PathVariable Long watchlistId,
                                                         @PathVariable Long movieId) {
        watchlistService.deleteMovieFromWatchlist(watchlistId, movieId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
