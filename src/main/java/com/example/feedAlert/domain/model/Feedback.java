package com.example.feedAlert.domain.model;

import com.example.feedAlert.domain.exception.InvalidRatingException;

import java.time.Instant;

public class Feedback {
    private Long feedbackId;
    private Course course;
    private User user;
    private Rating rating;
    private String comment;
    private boolean isUrgent;
    private Instant createdAt;

    public Feedback() {
    }

    public Feedback(Long feedbackId, Course course, User user, Rating rating, String comment, boolean isUrgent, Instant createdAt) {
        this.feedbackId = feedbackId;
        this.course = course;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.isUrgent = isUrgent;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getFeedbackId() {
        return feedbackId;
    }

    public Course getCourse() {
        return course;
    }

    public User getUser() {
        return user;
    }

    public Rating getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUrgent(boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long feedbackId;
        private Course course;
        private User user;
        private Rating rating;
        private String comment;
        private boolean isUrgent;
        private Instant createdAt;

        public Builder feedbackId(Long feedbackId) {
            this.feedbackId = feedbackId;
            return this;
        }

        public Builder course(Course course) {
            this.course = course;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder rating(Rating rating) {
            this.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder isUrgent(boolean isUrgent) {
            this.isUrgent = isUrgent;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Feedback build() {
            return new Feedback(feedbackId, course, user, rating, comment, isUrgent, createdAt);
        }
    }

    // Value Object para Rating
    public record Rating(Integer value) {
        private static final int MIN_RATING = 1;
        private static final int MAX_RATING = 5;

        public Rating {
            if (value == null) {
                throw new InvalidRatingException("Rating cannot be null");
            }
            if (value < MIN_RATING || value > MAX_RATING) {
                throw new InvalidRatingException(
                    String.format("Rating must be between %d and %d", MIN_RATING, MAX_RATING)
                );
            }
        }
    }

    // Domain logic
    public boolean requiresUrgentNotification() {
        return isUrgent;
    }
}
