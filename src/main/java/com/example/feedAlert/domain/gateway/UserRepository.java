package com.example.feedAlert.domain.gateway;

import com.example.feedAlert.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRoleName(String roleName);
}

