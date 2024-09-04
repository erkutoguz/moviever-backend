package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.model.Review;
import com.erkutoguz.moviever_backend.model.ReviewDocument;

public interface ReviewDocumentMapper {

    static ReviewDocument map(Review review) {
        return new ReviewDocument(review.getId(),review.getUser().getUsername(),review.getUser().getId(),review.getMovie().getId(),
                review.getMovie().getTitle(),review.getComment(),review.getCreatedAt().toString(),review.getLikeCount());
    }

}
