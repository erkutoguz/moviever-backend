package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.repository.CategoryRepository;
import com.erkutoguz.moviever_backend.util.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> retrieveAllCategories() {
        return CategoryMapper.map(categoryRepository.findAll());
    }
}
