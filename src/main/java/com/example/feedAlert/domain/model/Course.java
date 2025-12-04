package com.example.feedAlert.domain.model;

import java.time.Instant;

public class Course {
    private Long courseId;
    private String title;
    private String description;
    private Instant createdAt;

    public Course() {
    }

    public Course(Long courseId, String title, String description, Instant createdAt) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long courseId;
        private String title;
        private String description;
        private Instant createdAt;

        public Builder courseId(Long courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Course build() {
            return new Course(courseId, title, description, createdAt);
        }
    }
}
