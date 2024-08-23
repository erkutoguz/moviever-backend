package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);
   Optional<UserDetails> findByUsername(String username);
   Optional<User> findByOtp(String otp);
   Page<User> findAllByOrderByIdAsc(Pageable pageable);
}
