package com.erkutoguz.moviever_backend.dto.response;

import com.erkutoguz.moviever_backend.model.Review;

import java.util.Set;

public record MovieResponseWithDetails(Long id, String title, String director, Set<Review> reviews, int releaseYear, double rating, String posterUrl, int likeCount) {
}
