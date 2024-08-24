package com.erkutoguz.moviever_backend.dto.response;

import java.util.Set;

public record UserProfileResponse(String username,
                                  String firstname,
                                  String lastname,
                                  String aboutMe,
                                  String profilePictureUrl,
                                  Set<String> favouriteCategories) {
}
