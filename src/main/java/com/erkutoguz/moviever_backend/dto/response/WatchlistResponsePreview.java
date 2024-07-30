package com.erkutoguz.moviever_backend.dto.response;

import java.util.List;

public record WatchlistResponsePreview(Long id, List<MovieResponse> movies, String watchlistName) {
}
