package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.ReviewResponse;
import com.erkutoguz.moviever_backend.model.Review;

import java.util.List;

public interface ReviewMapper {
    static ReviewResponse map(Review review) {
        if (review == null) return null;
        return new ReviewResponse(review.getComment(), review.getCreatedAt(),
                review.getUpdatedAt(),review.getUser().getUsername(), review.getUser().getPictureUrl(), review.getLikeCount());
    }

    static List<ReviewResponse> map(List<Review> reviews) {
        if (reviews == null) return null;
        return reviews.stream().map(ReviewMapper::map).toList();
    }
}
