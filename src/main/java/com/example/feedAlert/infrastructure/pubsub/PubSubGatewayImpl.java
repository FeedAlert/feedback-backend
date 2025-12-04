package com.example.feedAlert.infrastructure.pubsub;

import com.example.feedAlert.domain.gateway.PubSubGateway;
import com.example.feedAlert.domain.model.Feedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.notification.mode", havingValue = "pubsub", matchIfMissing = true)
public class PubSubGatewayImpl implements PubSubGateway {

    private static final Logger log = LoggerFactory.getLogger(PubSubGatewayImpl.class);
    
    private final ObjectMapper objectMapper;
    private final PubSubTemplate pubSubTemplate;
    
    @Value("${app.pubsub.topic.feedback-events}")
    private String topicName;

    public PubSubGatewayImpl(ObjectMapper objectMapper, PubSubTemplate pubSubTemplate) {
        this.objectMapper = objectMapper;
        this.pubSubTemplate = pubSubTemplate;
    }

    @Override
    public void publishFeedbackEvent(Feedback feedback) {
        try {
            FeedbackEventPayload payload = FeedbackEventPayload.builder()
                .rating(feedback.getRating() != null ? feedback.getRating().value() : null)
                .comment(feedback.getComment())
                .isUrgent(feedback.isUrgent())
                .createdAt(feedback.getCreatedAt() != null ? feedback.getCreatedAt() : Instant.now())
                .student(StudentPayload.builder()
                    .userId(feedback.getUser().getUserId().toString())
                    .name(feedback.getUser().getName().value())
                    .email(feedback.getUser().getEmail().value())
                    .build())
                .course(CoursePayload.builder()
                    .courseId(feedback.getCourse().getCourseId().toString())
                    .title(feedback.getCourse().getTitle())
                    .build())
                .build();

            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("Publishing feedback event to topic {}: {}", topicName, jsonPayload);

            pubSubTemplate.publish(topicName, jsonPayload, 
                java.util.Map.of("messageId", UUID.randomUUID().toString()));
            
            log.info("Feedback event published successfully");
        } catch (Exception e) {
            log.error("Error publishing feedback event to Pub/Sub", e);
            throw new RuntimeException("Failed to publish feedback event", e);
        }
    }

    static class FeedbackEventPayload {
        private Integer rating;
        private String comment;
        
        @com.fasterxml.jackson.annotation.JsonProperty("isUrgent")
        private boolean isUrgent;
        
        @com.fasterxml.jackson.annotation.JsonProperty("createdAt")
        private Instant createdAt;
        
        private StudentPayload student;
        private CoursePayload course;

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public boolean isUrgent() { return isUrgent; }
        public void setUrgent(boolean isUrgent) { this.isUrgent = isUrgent; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public StudentPayload getStudent() { return student; }
        public void setStudent(StudentPayload student) { this.student = student; }
        public CoursePayload getCourse() { return course; }
        public void setCourse(CoursePayload course) { this.course = course; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private FeedbackEventPayload payload = new FeedbackEventPayload();
            
            public Builder rating(Integer rating) { payload.rating = rating; return this; }
            public Builder comment(String comment) { payload.comment = comment; return this; }
            public Builder isUrgent(boolean isUrgent) { payload.isUrgent = isUrgent; return this; }
            public Builder createdAt(Instant createdAt) { payload.createdAt = createdAt; return this; }
            public Builder student(StudentPayload student) { payload.student = student; return this; }
            public Builder course(CoursePayload course) { payload.course = course; return this; }
            public FeedbackEventPayload build() { return payload; }
        }
    }

    static class StudentPayload {
        private String userId;
        private String name;
        private String email;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private StudentPayload payload = new StudentPayload();
            
            public Builder userId(String userId) { payload.userId = userId; return this; }
            public Builder name(String name) { payload.name = name; return this; }
            public Builder email(String email) { payload.email = email; return this; }
            public StudentPayload build() { return payload; }
        }
    }

    static class CoursePayload {
        private String courseId;
        private String title;

        public String getCourseId() { return courseId; }
        public void setCourseId(String courseId) { this.courseId = courseId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private CoursePayload payload = new CoursePayload();
            
            public Builder courseId(String courseId) { payload.courseId = courseId; return this; }
            public Builder title(String title) { payload.title = title; return this; }
            public CoursePayload build() { return payload; }
        }
    }
}
