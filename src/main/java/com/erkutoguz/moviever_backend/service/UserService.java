package com.erkutoguz.moviever_backend.service;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.dto.response.UserDetailsResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserDocumentRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.UserDocumentMapper;
import com.erkutoguz.moviever_backend.util.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserDocumentRepository userDocumentRepository;
    private final ESProducer esProducer;
    private final DropboxService dropboxService;
    public UserService(UserRepository userRepository,
                       UserDocumentRepository userDocumentRepository,
                       ESProducer esProducer, DropboxService dropboxService) {
        this.userRepository = userRepository;
        this.userDocumentRepository = userDocumentRepository;
        this.esProducer = esProducer;
        this.dropboxService = dropboxService;
    }

    public String syncWithEs() {
        List<User> users = userRepository.findAll();
        userDocumentRepository.deleteAll();
        esProducer.sendUserDocumentList(UserDocumentMapper.map(users));
        return "successfully synchronized";

    }


    @Cacheable(value = "retrieveAllUsers", key = "#root.methodName + '-' + #pageNumber + '-' + #pageSize")
    public Map<String, Object> retrieveAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        final Page<User> users = userRepository.findAllByOrderByIdAsc(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("users", UserMapper.map(users));
        map.put("totalItems", users.getTotalElements());
        map.put("totalPages", users.getTotalPages());
        return map;
    }


    public UserDetailsResponse retrieveProfile(String username) throws IOException {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDetailsResponse(user.getFirstname(), user.getLastname(),user.getPictureUrl(), user.getAbout());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

    // Admin ops
    @CacheEvict(value = "retrieveAllUsers, retrieveAllWatchlists", allEntries = true)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        esProducer.sendDeleteUserMessage(userId);
        userRepository.delete(user);
    }

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public void uploadProfilePicture(String name, MultipartFile multipartFile) throws IOException, DbxException {
        User user = (User) userRepository.findByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String profileUrl = dropboxService.uploadImage("userProfile",name,multipartFile);
        user.setPictureUrl(profileUrl);
        userRepository.save(user);
    }

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public void deleteProfilePicture(String name) throws DbxException {
        User user = (User) userRepository.findByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        dropboxService.deleteImage(user.getPictureUrl());
    }

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public void updateUserStatus(UpdateUserDocumentStatusRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(request.newStatus());
        esProducer.updateUserDocumentStatus(new UpdateUserDocumentStatusRequest(request.userId(), request.newStatus()));
        userRepository.save(user);
    }
}
