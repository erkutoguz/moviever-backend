package com.erkutoguz.moviever_backend.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(Long id, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, String username, String pictureUrl, int likeCount) {
}
