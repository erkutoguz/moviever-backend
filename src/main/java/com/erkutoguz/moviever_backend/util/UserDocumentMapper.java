package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.model.UserDocument;

public interface UserDocumentMapper {
    static UserDocument map(User user) {
        return new UserDocument(user.getEmail(), user.getFirstname(), user.getLastname(), user.getUsername(),
                user.getRoles().stream().map(Role::getAuthority).toList(),user.getId(), user.isEnabled());
    }
}
