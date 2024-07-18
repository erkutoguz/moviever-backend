package com.erkutoguz.moviever_backend.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(String comment, LocalDateTime createdAt, LocalDateTime updatedAt, String username, String pictureUrl, int likeCount) {
}
