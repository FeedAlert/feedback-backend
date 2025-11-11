package com.example.feedAlert.application.mapper;

import com.example.feedAlert.application.dto.CourseResponse;
import com.example.feedAlert.domain.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    CourseResponse toResponse(Course course);
}

