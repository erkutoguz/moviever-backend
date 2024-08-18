package com.erkutoguz.moviever_backend.service;


import com.erkutoguz.moviever_backend.exception.InternalServerException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogService {

    private static final Logger log = LoggerFactory.getLogger(LogService.class);


    public Map<String, Object> showLogs(int page, int size, String type) {
        Map<String, Object> map = new HashMap<>();

        String rootPath = System.getProperty("user.dir");
        File folder = new File(rootPath+"/logs");
        List<String> logList = Arrays.stream(folder.list()).filter(l -> l.startsWith(type)).toList();
        Page<String> logsPage =  new PageImpl<>(logList.reversed());
        map.put("logs", logsPage.getContent());
        map.put("totalItems", logsPage.getTotalElements());
        map.put("totalPages", logsPage.getTotalPages());
        return map;
    }

    public Map<String, Object> downloadLog(String fileName) {
        Map<String, Object> map = new HashMap<>();
        try {
            String rootPath = System.getProperty("user.dir");
            File file = new File(rootPath  + "/logs/" + fileName);

            if (!file.exists()) {
                throw new ResourceNotFoundException("File not found");
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            map.put("resource", resource);
            map.put("fileLength", file.length());
            return map;

        } catch (IOException e) {
            throw new InternalServerException("Something went wrong");
        }
    }
}
