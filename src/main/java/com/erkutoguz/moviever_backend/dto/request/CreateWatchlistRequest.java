package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWatchlistRequest(@NotBlank(message = "Watchlist name can not be empty") String watchlistName) {

}
