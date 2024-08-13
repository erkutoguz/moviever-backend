package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminUserResponse;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.model.UserDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface UserDocumentMapper {
    static UserDocument map(User request) {
        UserDocument userDocument = new UserDocument();
        userDocument.setId(request.getId());
        userDocument.setEmail(request.getEmail());
        userDocument.setEnabled(request.isEnabled());
        userDocument.setRoles(request.getRoles());
        userDocument.setUsername(request.getUsername());
        userDocument.setFirstName(request.getFirstname());
        userDocument.setLastName(request.getLastname());
        return userDocument;
    }

    static List<UserDocument> map(List<User> request) {
        List<UserDocument> userDocumentList = new ArrayList<>();
        for (User u : request) {
            userDocumentList.add(map(u));
        }
        return userDocumentList;
    }

    static AdminUserResponse map(UserDocument request) {
        return new AdminUserResponse(request.getId(), request.getUsername(), request.getEmail(),
                request.getFirstName(), request.getLastName(), request.isEnabled(),
                request.getRoles().stream().map(Role::getAuthority).collect(Collectors.toSet()));
    }

    static List<AdminUserResponse> mapUserDocument(List<UserDocument> request) {
        List<AdminUserResponse> adminUserResponses = new ArrayList<>();
        for (UserDocument ud : request) {
            adminUserResponses.add(map(ud));
        }
        return adminUserResponses;
    }
}
