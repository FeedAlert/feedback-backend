package com.example.feedAlert.infrastructure.persistence.jpa;

import com.example.feedAlert.infrastructure.persistence.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaFeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
    
    @EntityGraph(attributePaths = {"course", "user", "user.role"})
    @Override
    Optional<FeedbackEntity> findById(Long id);
    
    @EntityGraph(attributePaths = {"course", "user", "user.role"})
    @Override
    List<FeedbackEntity> findAll();
    
    @Query("SELECT DISTINCT f FROM FeedbackEntity f " +
           "LEFT JOIN FETCH f.course " +
           "LEFT JOIN FETCH f.user u " +
           "LEFT JOIN FETCH u.role " +
           "WHERE f.course.courseId = :courseId")
    List<FeedbackEntity> findByCourseCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT DISTINCT f FROM FeedbackEntity f " +
           "LEFT JOIN FETCH f.course " +
           "LEFT JOIN FETCH f.user u " +
           "LEFT JOIN FETCH u.role " +
           "WHERE f.user.userId = :userId")
    List<FeedbackEntity> findByUserUserId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT f FROM FeedbackEntity f " +
           "LEFT JOIN FETCH f.course " +
           "LEFT JOIN FETCH f.user u " +
           "LEFT JOIN FETCH u.role " +
           "WHERE f.isUrgent = :isUrgent")
    List<FeedbackEntity> findByIsUrgent(@Param("isUrgent") Boolean isUrgent);
    
    @Query("SELECT DISTINCT f FROM FeedbackEntity f " +
           "LEFT JOIN FETCH f.course " +
           "LEFT JOIN FETCH f.user u " +
           "LEFT JOIN FETCH u.role " +
           "WHERE f.createdAt BETWEEN :startDate AND :endDate")
    List<FeedbackEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT DISTINCT f FROM FeedbackEntity f " +
           "LEFT JOIN FETCH f.course " +
           "LEFT JOIN FETCH f.user u " +
           "LEFT JOIN FETCH u.role " +
           "WHERE f.course.courseId = :courseId AND f.createdAt BETWEEN :startDate AND :endDate")
    List<FeedbackEntity> findByCourseIdAndCreatedAtBetween(
        @Param("courseId") Long courseId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );
}
