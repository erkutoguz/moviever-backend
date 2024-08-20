package com.erkutoguz.moviever_backend.dto.response;

import java.util.Set;

public record MovieResponse(Long id, String title, int releaseYear, double rating, String pictureUrl,
                            String trailerUrl, long viewCount, int likeCount, Set<CategoryResponse> categories) {
}
