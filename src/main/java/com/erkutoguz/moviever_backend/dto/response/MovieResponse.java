package com.erkutoguz.moviever_backend.dto.response;

import com.erkutoguz.moviever_backend.model.Category;

import java.util.Set;

public record MovieResponse(Long id, String title, int releaseYear, double rating, String posterUrl, int likeCount, Set<Category> categories) {
}
