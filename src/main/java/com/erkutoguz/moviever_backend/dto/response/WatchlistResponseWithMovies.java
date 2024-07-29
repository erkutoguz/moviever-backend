package com.erkutoguz.moviever_backend.dto.response;

import java.util.List;

public record WatchlistResponseWithMovies(Long id, List<MovieResponse> movieList, String watchlistName) {
}
