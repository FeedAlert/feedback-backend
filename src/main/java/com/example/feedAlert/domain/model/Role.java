package com.example.feedAlert.domain.model;

public class Role {
    private Long roleId;
    private String name;
    private String description;

    public static final String STUDENT = "STUDENT";
    public static final String ADMIN = "ADMIN";

    public Role() {
    }

    public Role(Long roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long roleId;
        private String name;
        private String description;

        public Builder roleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Role build() {
            return new Role(roleId, name, description);
        }
    }
}
