package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
   Page<Watchlist> findByUserId(Long userId, Pageable pageable);

    Page<Watchlist> findAllByOrderByIdAsc(Pageable pageable);
}
