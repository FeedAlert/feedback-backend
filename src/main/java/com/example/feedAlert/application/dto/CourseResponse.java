package com.example.feedAlert.application.dto;

import java.time.Instant;

public record CourseResponse(
    Long courseId,
    String title,
    String description,
    Instant createdAt
) {}

