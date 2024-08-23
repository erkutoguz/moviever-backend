package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record WatchlistMovieRequest(@NotNull(message = "Movie id cannot be empty")
                                    Long movieId) {

}
