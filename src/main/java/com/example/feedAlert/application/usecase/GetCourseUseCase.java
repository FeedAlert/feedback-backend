package com.example.feedAlert.application.usecase;

import com.example.feedAlert.application.dto.CourseResponse;
import com.example.feedAlert.application.mapper.CourseMapper;
import com.example.feedAlert.domain.gateway.CourseRepository;
import com.example.feedAlert.domain.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCourseUseCase {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

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

