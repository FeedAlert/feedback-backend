package com.example.feedAlert.domain.model;

import com.example.feedAlert.domain.exception.InvalidRatingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private Long feedbackId;
    private Course course;
    private User user;
    private Rating rating;
    private String comment;
    private boolean isUrgent;
    private Instant createdAt;

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

