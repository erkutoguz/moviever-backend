package com.erkutoguz.moviever_backend.dto.response;


public record AdminReviewResponse(Long id, String username, Long movieId,
                                  String movieName, String review, String createdAt, int likeCount) {
}
