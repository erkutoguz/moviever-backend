package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminReviewResponse;
import com.erkutoguz.moviever_backend.model.Review;
import com.erkutoguz.moviever_backend.model.ReviewDocument;

import java.util.ArrayList;
import java.util.List;

public interface ReviewDocumentMapper {
    static ReviewDocument map(Review review) {
        ReviewDocument reviewDocument = new ReviewDocument();
        reviewDocument.setId(review.getId());
        reviewDocument.setReview(review.getComment());
        reviewDocument.setMovieId(review.getMovie().getId());
        reviewDocument.setUsername( review.getUser().getUsername());
        reviewDocument.setCreatedAt(review.getCreatedAt().toString());
        if(review.getLiked() != null) {
            reviewDocument.setLikeCount(review.getLiked().size());
        } else {
            reviewDocument.setLikeCount(0);
        }

        reviewDocument.setMovieName(review.getMovie().getTitle());
        return reviewDocument;
    }

    static List<ReviewDocument> map(List<Review> reviews) {
        return reviews.stream().map(ReviewDocumentMapper::map).toList();
    }

    static AdminReviewResponse map(ReviewDocument reviewDocument) {
        return new AdminReviewResponse(reviewDocument.getId(),
                reviewDocument.getUsername(), reviewDocument.getMovieId(),
                reviewDocument.getMovieName(), reviewDocument.getReview(),
                reviewDocument.getCreatedAt(),reviewDocument.getLikeCount());
    }

    static List<AdminReviewResponse> mapReviewDocument(List<ReviewDocument> request) {
        List<AdminReviewResponse> adminReviewResponses = new ArrayList<>();
        for (ReviewDocument rd : request) {
                adminReviewResponses.add(map(rd));
        }
        return adminReviewResponses;
    }
}
