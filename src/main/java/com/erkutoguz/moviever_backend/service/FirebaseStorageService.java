package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class FirebaseStorageService {

    private final String bucketName = "moviever-media-storage.appspot.com";
    private final  UserRepository userRepository;
    public FirebaseStorageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @CacheEvict(value = "moviePosters", key = "#title")
    public String uploadPoster(MultipartFile multipartFile, String title) throws IOException {
        String fileName = getFileName(multipartFile, title,"movie");
        File file = convertToFile(multipartFile, fileName);
        String url = uploadImageToStorage(file, fileName);
        file.delete();
        return url;
    }

//    @CacheEvict(value = "profilePictures", key = "#username")
    public void uploadImage(MultipartFile multipartFile, String username) throws IOException {
        //TODO ilk register işleminde user'a profile picture seçtirme
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String fileName = getFileName(multipartFile, user.getUsername(), "user");
        File file = convertToFile(multipartFile, fileName);

        if(userHasExistingProfilePicture(user)) {
            deleteExistingProfilePicture(user);
        }

        String url = uploadImageToStorage(file, fileName);
        file.delete();
        updateUserProfilePicture(user, url);
    }

//    @Cacheable(value = "moviePosters", key = "#root.methodName + '' + #movie.title",unless = "#result==null")
    public String getPosterUrl(Movie movie) throws IOException {
        if(movie.getPictureUrl() == null || movie.getPictureUrl().isEmpty() || movie.getPictureUrl().trim().isEmpty()){
            return "";
        }
        String fileName = movie.getPictureUrl().substring(movie.getPictureUrl().lastIndexOf("/") + 1);

        BlobId blobId = BlobId.of(bucketName, fileName);
        Storage storage = getStorage();

        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
        URL signedUrl = blob.signUrl(1, TimeUnit.DAYS);

        return signedUrl.toString();
    }

//    @Cacheable(value = "profilePictures", key = "#root.methodName + '-' + #user.username",unless = "#result==null")
    public String getImageUrl(User user) throws IOException {
        if(user.getPictureUrl() == null || user.getPictureUrl().isEmpty() || user.getPictureUrl().trim().isEmpty()) {
             return "";
        }
        String fileName = user.getPictureUrl()
                .substring(user.getPictureUrl().lastIndexOf("/") + 1);

        BlobId blobId = BlobId.of(bucketName, fileName);
        Storage storage = getStorage();
        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
        URL signedUrl = blob.signUrl(1, TimeUnit.DAYS);

        return signedUrl.toString();
    }

//    @CacheEvict(value = "profilePictures", key = "#user.username")
    public void removeProfilePicture(String username) throws IOException {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        deleteExistingProfilePicture(user);
    }

    private void updateUserProfilePicture(User user, String url) {
        user.setPictureUrl(url);
        userRepository.save(user);
    }

    private String getFileName(MultipartFile multipartFile, String name, String type) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("File path must not be empty or null");
        }
        if(type.equals("user")) {
            return name + "_profile-picture." + getExtension(originalFileName);
        }
        return name + "_poster-picture." + getExtension(originalFileName);

    }

    private boolean userHasExistingProfilePicture(User user) {
        return user.getPictureUrl() != null && !user.getPictureUrl().isEmpty();
    }

    private Storage getStorage() throws IOException {
        InputStream inputStream = FirebaseStorageService.class.getClassLoader()
                .getResourceAsStream("moviever-media-storage-firebase-adminsdk-ri2bx-aad1182aff.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tmpFile = new File(fileName);
        try(FileOutputStream fos = new FileOutputStream(tmpFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tmpFile;
    }

    private String uploadImageToStorage(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        String contentType = "image/" + getExtension(fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        Storage storage = getStorage();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    private void deleteExistingProfilePicture(User user) throws IOException {
        String existingFileName = user.getPictureUrl().substring(user.getPictureUrl().lastIndexOf("/") + 1);
        BlobId blobId = BlobId.of(bucketName, existingFileName);
        Storage storage = getStorage();
        storage.delete(blobId);
        user.setPictureUrl("");
        userRepository.save(user);
    }
}
