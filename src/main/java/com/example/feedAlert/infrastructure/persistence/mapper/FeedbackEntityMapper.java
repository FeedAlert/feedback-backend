package com.example.feedAlert.infrastructure.persistence.mapper;

import com.example.feedAlert.domain.model.Course;
import com.example.feedAlert.domain.model.Feedback;
import com.example.feedAlert.domain.model.Role;
import com.example.feedAlert.domain.model.User;
import com.example.feedAlert.infrastructure.persistence.entity.CourseEntity;
import com.example.feedAlert.infrastructure.persistence.entity.FeedbackEntity;
import com.example.feedAlert.infrastructure.persistence.entity.RoleEntity;
import com.example.feedAlert.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEntityMapper {

    public Feedback toDomain(FeedbackEntity entity) {
        if (entity == null) {
            return null;
        }
        return Feedback.builder()
            .feedbackId(entity.getFeedbackId())
            .course(toCourseDomain(entity.getCourse()))
            .user(toUserDomain(entity.getUser()))
            .rating(new Feedback.Rating(entity.getRating()))
            .comment(entity.getComment())
            .isUrgent(entity.getIsUrgent())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public FeedbackEntity toEntity(Feedback domain) {
        if (domain == null) {
            return null;
        }
        return FeedbackEntity.builder()
            .feedbackId(domain.getFeedbackId())
            .course(toCourseEntity(domain.getCourse()))
            .user(toUserEntity(domain.getUser()))
            .rating(domain.getRating() != null ? domain.getRating().value() : null)
            .comment(domain.getComment())
            .isUrgent(domain.isUrgent())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    public Course toCourseDomain(CourseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Course.builder()
            .courseId(entity.getCourseId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public CourseEntity toCourseEntity(Course domain) {
        if (domain == null) {
            return null;
        }
        return CourseEntity.builder()
            .courseId(domain.getCourseId())
            .title(domain.getTitle())
            .description(domain.getDescription())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    public User toUserDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
            .userId(entity.getUserId())
            .name(new User.Name(entity.getName()))
            .email(new User.Email(entity.getEmail()))
            .password(entity.getPassword() != null ? new User.Password(entity.getPassword()) : null)
            .role(toRoleDomain(entity.getRole()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public UserEntity toUserEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserEntity.builder()
            .userId(domain.getUserId())
            .name(domain.getName() != null ? domain.getName().value() : null)
            .email(domain.getEmail() != null ? domain.getEmail().value() : null)
            .password(domain.getPassword() != null ? domain.getPassword().value() : null)
            .role(toRoleEntity(domain.getRole()))
            .createdAt(domain.getCreatedAt())
            .build();
    }

    private Role toRoleDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Role.builder()
            .roleId(entity.getRoleId())
            .name(entity.getName())
            .description(entity.getDescription())
            .build();
    }

    private RoleEntity toRoleEntity(Role domain) {
        if (domain == null) {
            return null;
        }
        return RoleEntity.builder()
            .roleId(domain.getRoleId())
            .name(domain.getName())
            .description(domain.getDescription())
            .build();
    }
}
