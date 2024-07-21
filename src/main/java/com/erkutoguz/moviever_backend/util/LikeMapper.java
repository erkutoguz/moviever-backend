package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.LikedReviewsResponse;
import com.erkutoguz.moviever_backend.model.Review;

import java.util.List;

public interface LikeMapper {

    static LikedReviewsResponse map(List<Review> reviews) {
        return new LikedReviewsResponse(reviews.stream().map(Review::getId).toList());
    }
}
