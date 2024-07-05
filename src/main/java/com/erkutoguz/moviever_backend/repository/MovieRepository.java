package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
