package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.UpdateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.UserDetailsResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;
    public UserService(UserRepository userRepository, FirebaseStorageService firebaseStorageService) {
        this.userRepository = userRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    public ResponseEntity<String> updateUser(String username, UpdateUserRequest request) {
        // TODO exceptions to everywhere
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(!request.firstname().isEmpty()) {
            user.setFirstname(request.firstname());
        }
        if(!request.lastname().isEmpty()) {
            user.setLastname(request.lastname());
        }
        if(!request.about().isEmpty()) {
            user.setAbout(request.about());
        }
        userRepository.save(user);
        return new ResponseEntity<String>("User successfully updated", HttpStatus.OK );
    }

    public UserDetailsResponse retrieveProfile(String username) throws IOException {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String pictureUrl = firebaseStorageService.getImageUrl(user);
        return new UserDetailsResponse(user.getFirstname(), user.getLastname(),pictureUrl, user.getAbout());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

    // Admin ops
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

}
