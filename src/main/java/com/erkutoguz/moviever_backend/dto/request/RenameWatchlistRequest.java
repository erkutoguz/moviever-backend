package com.erkutoguz.moviever_backend.dto.request;

public record RenameWatchlistRequest(String watchlistName) {
    public RenameWatchlistRequest{
        watchlistName = watchlistName != null ? watchlistName.trim() : null;
    }
}
