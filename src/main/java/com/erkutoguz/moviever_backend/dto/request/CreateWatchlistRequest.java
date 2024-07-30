package com.erkutoguz.moviever_backend.dto.request;

public record CreateWatchlistRequest(String watchlistName) {
    public CreateWatchlistRequest{
        watchlistName = watchlistName != null ? watchlistName.trim() : null;
    }
}
