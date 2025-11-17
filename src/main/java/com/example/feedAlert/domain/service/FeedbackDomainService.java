package com.example.feedAlert.domain.service;

import com.example.feedAlert.domain.model.Feedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FeedbackDomainService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackDomainService.class);

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
