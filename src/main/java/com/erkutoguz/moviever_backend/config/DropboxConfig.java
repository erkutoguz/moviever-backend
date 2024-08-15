package com.erkutoguz.moviever_backend.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {

    @Value("${spring.dropbox.token}")
    private String accessToken;

    @Bean
    public DbxClientV2 dbxClientV2() {
        DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("moviever-storage").build();
        return new DbxClientV2(dbxRequestConfig, accessToken);
    }
}
