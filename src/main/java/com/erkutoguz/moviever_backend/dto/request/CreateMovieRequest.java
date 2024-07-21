package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.model.CategoryType;

import java.util.Set;

public record CreateMovieRequest(String title, String director, int releaseYear, String pictureUrl, String trailerUrl,double rating, Set<CategoryType> categories) {
}
