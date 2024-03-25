package com.tma.digital_blog.repository;

import com.tma.digital_blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);
    Optional<User> findUserByUsername(String username);
    Page<User> findUsersByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
