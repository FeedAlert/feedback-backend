package com.example.feedAlert.infrastructure.pubsub;

import com.example.feedAlert.domain.gateway.PubSubGateway;
import com.example.feedAlert.domain.model.Feedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PubSubGatewayImpl implements PubSubGateway {

    private final ObjectMapper objectMapper;
    private final PubSubTemplate pubSubTemplate;
    
    @Value("${app.pubsub.topic.feedback-events}")
    private String topicName;

    @Override
    public void publishFeedbackEvent(Feedback feedback) {
        try {
            // Criar payload conforme especificação
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

            // Publicar no Pub/Sub usando Spring Cloud GCP
            pubSubTemplate.publish(topicName, jsonPayload, 
                java.util.Map.of("messageId", UUID.randomUUID().toString()));
            
            log.info("Feedback event published successfully");
        } catch (Exception e) {
            log.error("Error publishing feedback event to Pub/Sub", e);
            throw new RuntimeException("Failed to publish feedback event", e);
        }
    }

    // DTOs para payload do Pub/Sub
    @lombok.Data
    @lombok.Builder
    static class FeedbackEventPayload {
        private Integer rating;
        private String comment;
        private Boolean isUrgent;
        private Instant createdAt;
        private StudentPayload student;
        private CoursePayload course;
    }

    @lombok.Data
    @lombok.Builder
    static class StudentPayload {
        private String userId;
        private String name;
        private String email;
    }

    @lombok.Data
    @lombok.Builder
    static class CoursePayload {
        private String courseId;
        private String title;
    }
}
