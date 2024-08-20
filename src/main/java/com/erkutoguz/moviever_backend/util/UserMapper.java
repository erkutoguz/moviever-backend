package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminUserResponse;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public interface UserMapper {

    static List<AdminUserResponse> map(Page<User> users) {
        return users.stream().map(UserMapper::map).toList();
    }

    static AdminUserResponse map(User user) {
        return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstname(),
                user.getLastname(), user.isEnabled(), user.getRoles()
                .stream().map(Role::getAuthority).collect(Collectors.toSet()), user.getPictureUrl());
    }
}
