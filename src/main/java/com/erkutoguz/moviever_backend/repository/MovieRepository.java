package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.Category;
import com.erkutoguz.moviever_backend.model.Movie;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAll(Sort sort);
}
