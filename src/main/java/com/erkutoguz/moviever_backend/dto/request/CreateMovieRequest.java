package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.model.CategoryType;

import java.util.Set;

public record CreateMovieRequest(String title, String director, int releaseYear,
                                 String pictureUrl, String trailerUrl,double rating,
                                 Set<CategoryType> categories) {
    public CreateMovieRequest{
        title = title != null ? title.trim() : null;
        director = director != null ? director.trim() : null;
        pictureUrl = pictureUrl != null ? pictureUrl.trim() : null;
        trailerUrl = trailerUrl != null ? trailerUrl.trim() : null;
    }
}
