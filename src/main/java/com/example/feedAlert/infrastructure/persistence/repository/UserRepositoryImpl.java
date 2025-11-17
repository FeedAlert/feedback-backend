package com.example.feedAlert.infrastructure.persistence.repository;

import com.example.feedAlert.domain.gateway.UserRepository;
import com.example.feedAlert.domain.model.User;
import com.example.feedAlert.infrastructure.persistence.entity.UserEntity;
import com.example.feedAlert.infrastructure.persistence.jpa.JpaUserRepository;
import com.example.feedAlert.infrastructure.persistence.mapper.FeedbackEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final FeedbackEntityMapper mapper;

    public UserRepositoryImpl(JpaUserRepository jpaRepository, FeedbackEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toUserEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        // Recarregar com relacionamentos
        return jpaRepository.findById(saved.getUserId())
            .flatMap(u -> jpaRepository.findByEmail(u.getEmail()))
            .map(mapper::toUserDomain)
            .orElseThrow(() -> new RuntimeException("Failed to save user"));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findById(userId)
            .map(mapper::toUserDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(mapper::toUserDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toUserDomain)
            .toList();
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return jpaRepository.findByRoleName(roleName).stream()
            .map(mapper::toUserDomain)
            .toList();
    }
}
