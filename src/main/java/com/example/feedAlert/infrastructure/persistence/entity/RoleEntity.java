package com.example.feedAlert.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public RoleEntity() {
    }

    public RoleEntity(Long roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
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

        public RoleEntity build() {
            return new RoleEntity(roleId, name, description);
        }
    }
}
