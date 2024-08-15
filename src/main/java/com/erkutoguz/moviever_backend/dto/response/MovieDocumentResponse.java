package com.erkutoguz.moviever_backend.dto.response;

import java.util.Set;

public record MovieDocumentResponse(Long movieId, String title, String posterUrl, int releaseYear,
                                    Set<CategoryResponse> categories) {
}
