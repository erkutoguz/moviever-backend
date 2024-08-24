package com.erkutoguz.moviever_backend.dto.request;

import java.util.List;

public record RecommendedMovieRequest(long id, double rating, int likes, long view, List<String> categories) {
}
