package com.erkutoguz.moviever_backend.dto.response;

import java.util.Set;

public record MovieResponseWithDetails(Long id, String title, String director,  int releaseYear,
                                       double rating, String pictureUrl,String trailerUrl,
                                       long viewCount, int likeCount, boolean isUserLiked, Set<CategoryResponse> categories) {
}
