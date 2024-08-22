package com.erkutoguz.moviever_backend.controller;


import com.erkutoguz.moviever_backend.service.ESReviewDocumentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewDocumentController {

    private final ESReviewDocumentService esReviewDocumentService;

    public ReviewDocumentController(ESReviewDocumentService esReviewDocumentService) {
        this.esReviewDocumentService = esReviewDocumentService;
    }

    @GetMapping("/search/{partialInput}")
    public Map<String, Object> searchReviewsAutoSuggest(
            @PathVariable String partialInput,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "1") int size)
            throws IOException {
        return esReviewDocumentService.searchReviewAutoSuggestV2(partialInput, page, size);
    }
}
