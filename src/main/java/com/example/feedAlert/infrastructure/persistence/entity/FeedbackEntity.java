package com.example.feedAlert.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_feedback")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public FeedbackEntity() {
    }

    public FeedbackEntity(Long feedbackId, CourseEntity course, UserEntity user, Integer rating, 
                         String comment, Boolean isUrgent, Instant createdAt) {
        this.feedbackId = feedbackId;
        this.course = course;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.isUrgent = isUrgent != null ? isUrgent : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isUrgent == null) {
            isUrgent = false;
        }
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent != null ? isUrgent : false;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long feedbackId;
        private CourseEntity course;
        private UserEntity user;
        private Integer rating;
        private String comment;
        private Boolean isUrgent = false;
        private Instant createdAt;

        public Builder feedbackId(Long feedbackId) {
            this.feedbackId = feedbackId;
            return this;
        }

        public Builder course(CourseEntity course) {
            this.course = course;
            return this;
        }

        public Builder user(UserEntity user) {
            this.user = user;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder isUrgent(Boolean isUrgent) {
            this.isUrgent = isUrgent != null ? isUrgent : false;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FeedbackEntity build() {
            return new FeedbackEntity(feedbackId, course, user, rating, comment, isUrgent, createdAt);
        }
    }
}
