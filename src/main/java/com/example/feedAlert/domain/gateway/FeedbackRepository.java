package com.example.feedAlert.domain.gateway;

import com.example.feedAlert.domain.model.Feedback;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {
    Feedback save(Feedback feedback);
    Optional<Feedback> findById(Long feedbackId);
    List<Feedback> findAll();
    List<Feedback> findByCourseId(Long courseId);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByIsUrgent(boolean isUrgent);
    List<Feedback> findByCreatedAtBetween(Instant startDate, Instant endDate);
    List<Feedback> findByCourseIdAndCreatedAtBetween(Long courseId, Instant startDate, Instant endDate);
}

