package com.example.feedAlert.infrastructure.persistence.jpa;

import com.example.feedAlert.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.role WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);
    
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.role WHERE u.role.name = :roleName")
    List<UserEntity> findByRoleName(@Param("roleName") String roleName);
}
