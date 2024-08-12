package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.UpdateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.UserDetailsResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserDocumentRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.UserDocumentMapper;
import com.erkutoguz.moviever_backend.util.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserDocumentRepository userDocumentRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ESProducer esProducer;
    public UserService(UserRepository userRepository, UserDocumentRepository userDocumentRepository, FirebaseStorageService firebaseStorageService, ESProducer esProducer) {
        this.userRepository = userRepository;
        this.userDocumentRepository = userDocumentRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.esProducer = esProducer;
    }

    public String syncWithEs() {
        List<User> users = userRepository.findAll();
        userDocumentRepository.deleteAll();
        esProducer.sendUserDocumentList(UserDocumentMapper.map(users));
        return "successfully synchronized";

    }

    public Map<String, Object> retrieveAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        final Page<User> users = userRepository.findAllByOrderByIdAsc(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("users", UserMapper.map(users));
        map.put("totalItems", users.getTotalElements());
        map.put("totalPages", users.getTotalPages());
        return map;
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
