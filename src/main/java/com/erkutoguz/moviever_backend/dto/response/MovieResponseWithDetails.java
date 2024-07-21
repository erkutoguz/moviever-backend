package com.erkutoguz.moviever_backend.dto.response;

import com.erkutoguz.moviever_backend.model.Category;

import java.util.List;
import java.util.Set;

public record MovieResponseWithDetails(Long id, String title, String director, List<ReviewResponse> reviews, int releaseYear, double rating, String pictureUrl,String trailerUrl, int likeCount, Set<Category> categories) {
}
