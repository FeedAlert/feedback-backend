package com.example.feedAlert.infrastructure.persistence.repository;

import com.example.feedAlert.domain.gateway.FeedbackRepository;
import com.example.feedAlert.domain.model.Feedback;
import com.example.feedAlert.infrastructure.persistence.entity.FeedbackEntity;
import com.example.feedAlert.infrastructure.persistence.jpa.JpaFeedbackRepository;
import com.example.feedAlert.infrastructure.persistence.mapper.FeedbackEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final JpaFeedbackRepository jpaRepository;
    private final FeedbackEntityMapper mapper;

    public FeedbackRepositoryImpl(JpaFeedbackRepository jpaRepository, FeedbackEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Feedback save(Feedback feedback) {
        FeedbackEntity entity = mapper.toEntity(feedback);
        FeedbackEntity saved = jpaRepository.save(entity);
        // Recarregar com relacionamentos
        return jpaRepository.findById(saved.getFeedbackId())
            .map(mapper::toDomain)
            .orElseThrow(() -> new RuntimeException("Failed to save feedback"));
    }

    @Override
    public Optional<Feedback> findById(Long feedbackId) {
        return jpaRepository.findById(feedbackId)
            .map(mapper::toDomain);
    }

    @Override
    public List<Feedback> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Feedback> findByCourseId(Long courseId) {
        return jpaRepository.findByCourseCourseId(courseId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        return jpaRepository.findByUserUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Feedback> findByIsUrgent(boolean isUrgent) {
        return jpaRepository.findByIsUrgent(isUrgent).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Feedback> findByCreatedAtBetween(Instant startDate, Instant endDate) {
        return jpaRepository.findByCreatedAtBetween(startDate, endDate).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Feedback> findByCourseIdAndCreatedAtBetween(Long courseId, Instant startDate, Instant endDate) {
        return jpaRepository.findByCourseIdAndCreatedAtBetween(courseId, startDate, endDate).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
