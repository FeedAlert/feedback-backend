package com.example.feedAlert.domain.gateway;

import com.example.feedAlert.domain.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    Optional<Course> findById(Long courseId);
    List<Course> findAll();
}

