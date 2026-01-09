package com.example.feedAlert.application.dto;

public record LoginResponse(
    String token,
    String type,
    Long userId,
    String email,
    String name,
    String role
) {
    public static LoginResponse of(String token, Long userId, String email, String name, String role) {
        return new LoginResponse(token, "Bearer", userId, email, name, role);
    }
}

