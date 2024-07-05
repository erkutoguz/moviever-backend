package com.erkutoguz.moviever_backend.dto.response;

import com.erkutoguz.moviever_backend.model.Movie;

import java.util.Set;

public record WatchlistResponse(Long id, Set<Movie> movieList, String watchlistName) {
}
