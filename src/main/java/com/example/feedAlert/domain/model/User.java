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
public class User {
    private Long userId;
    private Name name;
    private Email email;
    private Role role;
    private Instant createdAt;

    // Value Objects
    public record Name(String value) {
        public Name {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or blank");
            }
        }
    }

    public record Email(String value) {
        public Email {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
            if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email format is invalid");
            }
        }
    }
}

