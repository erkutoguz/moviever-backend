package com.erkutoguz.moviever_backend.dto.request;

public record ReviewRequest(String comment) {
    public ReviewRequest{
        comment = comment != null ? comment.trim() : null;
    }
}
