package com.erkutoguz.moviever_backend.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadMoviePoster(MultipartFile image, String movieName) {
        try {
            return uploadImage(image, "movies", movieName);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String uploadUserAvatar(MultipartFile image, String username) {
        try {
            return uploadImage(image, "users", username);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private String uploadImage(MultipartFile image, String folderName, String fileName) throws IOException {
        Map params = ObjectUtils.asMap(
                "folder","/"+folderName,
                "resource_type","image",
                "unique_filename", false,
                "public_id",fileName,
                "overwrite", true);
        var res = cloudinary.uploader().upload(image.getInputStream().readAllBytes(), params);
        return res.get("secure_url").toString();
    }

    public String deleteUserAvatar(String username){
        String res = deleteImage("users/"+username);
        log.info("res is {}", res);
        return res;
    }

    public String deleteMoviePoster(String movieName){
        return deleteImage("movies/"+movieName);
    }

    private String deleteImage(String publicId) {
        try {

            cloudinary.uploader().destroy(publicId,ObjectUtils.asMap("invalidate",true));
            return "Image successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
