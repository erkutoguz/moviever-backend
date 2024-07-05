package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.CreateWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.WatchlistMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponse;
import com.erkutoguz.moviever_backend.model.Watchlist;
import com.erkutoguz.moviever_backend.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public Set<Watchlist> retrieveUserWatchlists(Principal principal) {
        return watchlistService.retrieveUserWatchlists(principal);
    }

    @GetMapping("/{watchlistId}/movies")
    public WatchlistResponse retrieveWatchlist(@PathVariable Long watchlistId){
        return watchlistService.retrieveWatchlist(watchlistId);
    }

    @PostMapping
    public ResponseEntity<Void> createWatchlist(CreateWatchlistRequest request, Principal principal) {
        watchlistService.createWatchlist(request, principal);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/{watchlistId}")
    public ResponseEntity<Void> addMovieToWatchlist(@PathVariable Long watchlistId, @RequestBody WatchlistMovieRequest request) {
        watchlistService.addMovieToWatchlist(watchlistId, request);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(watchlistId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}/movies/{movieId}")
    public ResponseEntity<Void> deleteMovieFromWatchlist(@PathVariable Long watchlistId, @PathVariable Long movieId) {
        watchlistService.deleteMovieFromWatchlist(watchlistId, movieId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
