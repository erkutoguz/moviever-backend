package com.erkutoguz.moviever_backend.dto.response;

import java.util.List;

public record WatchlistResponse(Long id, List<MovieResponse> movieList, String watchlistName) {
}
