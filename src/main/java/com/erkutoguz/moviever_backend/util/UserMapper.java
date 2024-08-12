package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminUserResponse;
import com.erkutoguz.moviever_backend.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserMapper {

    static List<AdminUserResponse> map(Page<User> users) {
        return users.stream().map(UserMapper::map).toList();
    }

    static AdminUserResponse map(User user) {
        return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstname(),
                user.getLastname(), user.isEnabled(), user.getRoles());
    }
}
