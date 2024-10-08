package com.erkutoguz.moviever_backend.dto.response;

import java.util.Set;

public record AdminUserResponse(Long id, String username, String email, String firstName,
                                String lastName,boolean enabled, Set<String> roles, String pictureUrl) {
}
