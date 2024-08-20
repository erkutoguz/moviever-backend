package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {
    Optional<IpAddress> findByIp(String ip);
}
