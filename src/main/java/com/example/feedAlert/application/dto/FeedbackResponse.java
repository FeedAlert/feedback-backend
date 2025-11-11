package com.example.feedAlert.application.dto;

import java.time.Instant;

public record FeedbackResponse(
    Long feedbackId,
    CourseResponse course,
    StudentResponse student,
    Integer rating,
    String comment,
    Boolean isUrgent,
    Instant createdAt
) {
    public record CourseResponse(
        Long courseId,
        String title,
        String description
    ) {}
    
    public record StudentResponse(
        Long userId,
        String name,
        String email
    ) {}
}

