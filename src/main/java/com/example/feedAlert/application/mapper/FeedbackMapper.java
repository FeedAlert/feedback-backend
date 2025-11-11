package com.example.feedAlert.application.mapper;

import com.example.feedAlert.application.dto.FeedbackResponse;
import com.example.feedAlert.domain.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "rating", expression = "java(feedback.getRating() != null ? feedback.getRating().value() : null)")
    @Mapping(target = "student", expression = "java(toStudentResponse(feedback.getUser()))")
    @Mapping(target = "course", expression = "java(toCourseResponse(feedback.getCourse()))")
    @Mapping(target = "isUrgent", expression = "java(Boolean.valueOf(feedback.isUrgent()))")
    FeedbackResponse toResponse(Feedback feedback);

    default FeedbackResponse.StudentResponse toStudentResponse(com.example.feedAlert.domain.model.User user) {
        if (user == null) {
            return null;
        }
        return new FeedbackResponse.StudentResponse(
            user.getUserId(),
            user.getName() != null ? user.getName().value() : null,
            user.getEmail() != null ? user.getEmail().value() : null
        );
    }

    default FeedbackResponse.CourseResponse toCourseResponse(com.example.feedAlert.domain.model.Course course) {
        if (course == null) {
            return null;
        }
        return new FeedbackResponse.CourseResponse(
            course.getCourseId(),
            course.getTitle(),
            course.getDescription()
        );
    }
}

