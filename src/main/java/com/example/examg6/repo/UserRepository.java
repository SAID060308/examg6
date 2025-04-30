package com.example.examg6.repo;

import com.example.examg6.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByVerifiedCode(String verified_code);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
