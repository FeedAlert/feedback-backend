package com.example.feedAlert.application.usecase;

import com.example.feedAlert.application.dto.CourseResponse;
import com.example.feedAlert.application.mapper.CourseMapper;
import com.example.feedAlert.domain.gateway.CourseRepository;
import com.example.feedAlert.domain.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCourseUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCourseUseCase.class);
    
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public GetCourseUseCase(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public CourseResponse findById(Long courseId) {
        log.info("Finding course by ID: {}", courseId);
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        return courseMapper.toResponse(course);
    }

    public List<CourseResponse> findAll() {
        log.info("Finding all courses");
        return courseRepository.findAll().stream()
            .map(courseMapper::toResponse)
            .toList();
    }
}
