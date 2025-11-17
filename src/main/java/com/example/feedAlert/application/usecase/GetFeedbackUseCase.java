package com.example.feedAlert.application.usecase;

import com.example.feedAlert.application.dto.FeedbackResponse;
import com.example.feedAlert.application.mapper.FeedbackMapper;
import com.example.feedAlert.domain.gateway.FeedbackRepository;
import com.example.feedAlert.domain.model.Feedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFeedbackUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetFeedbackUseCase.class);
    
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    public GetFeedbackUseCase(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
    }

    public FeedbackResponse findById(Long feedbackId) {
        log.info("Finding feedback by ID: {}", feedbackId);
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new IllegalArgumentException("Feedback not found: " + feedbackId));
        return feedbackMapper.toResponse(feedback);
    }

    public List<FeedbackResponse> findAll() {
        log.info("Finding all feedbacks");
        return feedbackRepository.findAll().stream()
            .map(feedbackMapper::toResponse)
            .toList();
    }

    public List<FeedbackResponse> findByCourseId(Long courseId) {
        log.info("Finding feedbacks by course ID: {}", courseId);
        return feedbackRepository.findByCourseId(courseId).stream()
            .map(feedbackMapper::toResponse)
            .toList();
    }

    public List<FeedbackResponse> findByUserId(Long userId) {
        log.info("Finding feedbacks by user ID: {}", userId);
        return feedbackRepository.findByUserId(userId).stream()
            .map(feedbackMapper::toResponse)
            .toList();
    }

    public List<FeedbackResponse> findUrgentFeedbacks() {
        log.info("Finding urgent feedbacks");
        return feedbackRepository.findByIsUrgent(true).stream()
            .map(feedbackMapper::toResponse)
            .toList();
    }
}
