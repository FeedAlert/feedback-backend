package com.example.feedAlert.domain.service;

import com.example.feedAlert.domain.model.Feedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackDomainService {

    public boolean shouldNotifyAdmins(Feedback feedback) {
        if (feedback == null) {
            return false;
        }
        return feedback.requiresUrgentNotification();
    }

    public void validateFeedback(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }
        if (feedback.getCourse() == null || feedback.getCourse().getCourseId() == null) {
            throw new IllegalArgumentException("Feedback must have a valid course");
        }
        if (feedback.getUser() == null || feedback.getUser().getUserId() == null) {
            throw new IllegalArgumentException("Feedback must have a valid user");
        }
        if (feedback.getRating() == null) {
            throw new IllegalArgumentException("Feedback must have a rating");
        }
    }
}

