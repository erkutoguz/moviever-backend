package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminReviewResponse;
import com.erkutoguz.moviever_backend.model.Review;

import java.util.List;

public interface ReviewMapper {

    static AdminReviewResponse map(Review review) {
        if (review == null) return null;
        return new AdminReviewResponse(review.getId(),review.getUser().getUsername(), review.getMovie().getId()
                ,review.getMovie().getTitle(), review.getComment(), review.getCreatedAt().toString()
                ,review.getLikeCount());
    }

    static List<AdminReviewResponse> map(List<Review> reviews) {
        if (reviews == null) return null;
        return reviews.stream().map(ReviewMapper::map).toList();
    }

}
