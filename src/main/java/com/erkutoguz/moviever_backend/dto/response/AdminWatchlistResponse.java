package com.erkutoguz.moviever_backend.dto.response;


public record AdminWatchlistResponse(Long id, Long userId,String username, String watchlistName) {}