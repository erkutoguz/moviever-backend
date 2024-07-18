package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> retrieveAllCategories() {
        return categoryService.retrieveAllCategories();
    }
}
