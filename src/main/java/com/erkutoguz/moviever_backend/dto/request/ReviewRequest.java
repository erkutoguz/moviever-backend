package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(@NotBlank(message = "Review can not be empty")String comment) {

}
