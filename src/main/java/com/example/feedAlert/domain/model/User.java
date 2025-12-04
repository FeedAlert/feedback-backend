package com.example.feedAlert.domain.model;

import java.time.Instant;

public class User {
    private Long userId;
    private Name name;
    private Email email;
    private Role role;
    private Instant createdAt;

    public User() {
    }

    public User(Long userId, Name name, Email email, Role role, Instant createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private Name name;
        private Email email;
        private Role role;
        private Instant createdAt;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return new User(userId, name, email, role, createdAt);
        }
    }

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
