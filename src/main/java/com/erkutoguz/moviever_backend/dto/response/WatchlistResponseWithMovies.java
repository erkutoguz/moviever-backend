package com.erkutoguz.moviever_backend.dto.response;

import java.util.List;

public record WatchlistResponseWithMovies(Long id, long totalElements, int totalPages, List<MovieResponse> movieList, String watchlistName) {
}
