package com.erkutoguz.moviever_backend.service;

import com.dropbox.core.DbxException;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.dto.response.UserDetailsResponse;
import com.erkutoguz.moviever_backend.dto.response.UserProfileResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.Category;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserRepository;
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
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ESProducer esProducer;
    private final DropboxService dropboxService;
    public UserService(UserRepository userRepository,
                       ESProducer esProducer, DropboxService dropboxService) {
        this.userRepository = userRepository;
        this.esProducer = esProducer;
        this.dropboxService = dropboxService;
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Admin ops
    @CacheEvict(value = {"retrieveAllUsers", "retrieveAllWatchlists"}, allEntries = true)
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

    public UserProfileResponse retrieveAnotherUserProfile(String username) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Integer> likedMoviesCategoriesCountMap = new HashMap<>();

        for(Movie m : user.getLikedMovies()) {
            for(Category c : m.getCategories()) {
                String categoryName = c.getCategoryName().name();
                likedMoviesCategoriesCountMap.merge(categoryName, 1, Integer::sum);
            }
        }

        String firstCategoryName = null;
        String secondCategoryName = null;
        String thirdCategoryName = null;
        int firstCategoryCount = Integer.MIN_VALUE;
        int secondCategoryCount = Integer.MIN_VALUE;
        int thirdCategoryCount = Integer.MIN_VALUE;

        for (String k : likedMoviesCategoriesCountMap.keySet()) {
            if(likedMoviesCategoriesCountMap.get(k) > firstCategoryCount) {
                thirdCategoryCount = secondCategoryCount;
                thirdCategoryName = secondCategoryName;
                secondCategoryCount = firstCategoryCount;
                secondCategoryName = firstCategoryName;
                firstCategoryName = k;
                firstCategoryCount = likedMoviesCategoriesCountMap.get(k);
            } else if(likedMoviesCategoriesCountMap.get(k) > secondCategoryCount) {
                thirdCategoryCount = secondCategoryCount;
                thirdCategoryName = secondCategoryName;
                secondCategoryName = k;
                secondCategoryCount = likedMoviesCategoriesCountMap.get(k);
            } else if(likedMoviesCategoriesCountMap.get(k) > thirdCategoryCount) {
                thirdCategoryName = k;
                thirdCategoryCount = likedMoviesCategoriesCountMap.get(k);
            }
        }
        Set<String> favouriteCategories;
        if(firstCategoryName == null) {
            favouriteCategories = Set.of();
        } else if(secondCategoryName == null) {
            favouriteCategories = Set.of(firstCategoryName);
        } else if(thirdCategoryName == null){
            favouriteCategories = Set.of(firstCategoryName, secondCategoryName);
        } else {
            favouriteCategories = Set.of(firstCategoryName, secondCategoryName, thirdCategoryName);
        }
        return new UserProfileResponse(user.getUsername(), user.getFirstname(),user.getLastname(),
                user.getAbout(), user.getPictureUrl(), favouriteCategories);

    }
}
