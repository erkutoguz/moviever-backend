package com.erkutoguz.moviever_backend.controller;


import com.erkutoguz.moviever_backend.service.ESUserDocumentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserDocumentController {

    private final ESUserDocumentService userDocumentService;

    public UserDocumentController(ESUserDocumentService userDocumentService) {
        this.userDocumentService = userDocumentService;
    }
//TODO BUNU ADMIN SERVİCE'E al
    @GetMapping("/search/{partialInput}")
    public Map<String, Object> searchUsersAutoSuggest(
            @PathVariable String partialInput,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "1") int size)
            throws IOException {
        return userDocumentService.searchUsersAutoSuggest(partialInput, page, size);
    }

}
