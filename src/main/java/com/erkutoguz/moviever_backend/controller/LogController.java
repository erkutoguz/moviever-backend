package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.service.LogService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private final LogService logService;
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/show-logs")
    public Map<String, Object> showUserLogs(@RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(name = "size", defaultValue = "1") int size,
                                            @RequestParam(name="type", defaultValue = "user") String type) {
        return logService.showLogs(page, size, type);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadLog(@PathVariable String fileName) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        Map<String, Object> map = logService.downloadLog(fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength((Long) map.get("fileLength"))
                .body((InputStreamResource) map.get("resource"));
    }


}
