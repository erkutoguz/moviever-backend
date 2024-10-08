package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.CategoryMovieCountResponse;
import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.model.Category;
import com.erkutoguz.moviever_backend.repository.CategoryRepository;
import com.erkutoguz.moviever_backend.util.CategoryMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Cacheable(value = "allCategories", key = "#root.methodName", unless = "#result==null")
    public Set<CategoryResponse> retrieveAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryMapper.map(new HashSet<>(categories));
    }

    public List<CategoryMovieCountResponse> retrieveMovieCountForEachCategory() {
        return categoryRepository.findAll().stream().map(
                c -> new CategoryMovieCountResponse(c.getCategoryName().name(), c.getMovies().size())).toList();

    }
}
