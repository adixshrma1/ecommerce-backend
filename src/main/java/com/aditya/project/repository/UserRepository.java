package com.aditya.project.repository;

import com.aditya.project.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank @Size(min = 3, max = 20) String username);

    boolean existsByEmail(@NotBlank @Size(min = 3, max = 20) String username);
}

