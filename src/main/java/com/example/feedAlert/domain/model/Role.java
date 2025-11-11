package com.example.feedAlert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long roleId;
    private String name;
    private String description;

    public static final String STUDENT = "STUDENT";
    public static final String ADMIN = "ADMIN";
}

