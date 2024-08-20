package com.erkutoguz.moviever_backend.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class DropboxService {

    private final DbxClientV2 client;

    public DropboxService(DbxClientV2 client) {
        this.client = client;
    }

    public String uploadImage(String type, String name, MultipartFile image) throws IOException, DbxException {
        if(type.equals("userProfile")) {
            return uploadImageToDropbox("/user_profiles/", name, image);
        } else if (type.equals("moviePoster")) {
            return uploadImageToDropbox("/movie_posters/", name, image);
        }
        return "";
    }

    public void deleteImage(String path) throws DbxException {
        client.files().deleteV2(path);
    }

    private String uploadImageToDropbox(String folderPath, String name, MultipartFile image) throws DbxException, IOException {
        String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        String path = folderPath + name + extension;
        client.files().uploadBuilder(path)
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(image.getInputStream());
        SharedLinkMetadata builder = client.sharing().createSharedLinkWithSettings(path);
        String dbxUrl = builder.getUrl();
        return dbxUrl.substring(0, dbxUrl.lastIndexOf("?")) + "?dl=1";
    }


    private String uploadImageToDropbox2(String folderPath, String name, MultipartFile image) throws DbxException, IOException {
        return null;
    }




}
