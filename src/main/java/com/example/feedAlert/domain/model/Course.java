package com.example.feedAlert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long courseId;
    private String title;
    private String description;
    private Instant createdAt;
}

