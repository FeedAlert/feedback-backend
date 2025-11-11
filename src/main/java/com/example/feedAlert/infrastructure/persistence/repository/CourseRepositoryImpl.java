package com.example.feedAlert.infrastructure.persistence.repository;

import com.example.feedAlert.domain.gateway.CourseRepository;
import com.example.feedAlert.domain.model.Course;
import com.example.feedAlert.infrastructure.persistence.entity.CourseEntity;
import com.example.feedAlert.infrastructure.persistence.jpa.JpaCourseRepository;
import com.example.feedAlert.infrastructure.persistence.mapper.FeedbackEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final JpaCourseRepository jpaRepository;
    private final FeedbackEntityMapper mapper;

    @Override
    public Course save(Course course) {
        CourseEntity entity = mapper.toCourseEntity(course);
        CourseEntity saved = jpaRepository.save(entity);
        // Recarregar após salvar
        return jpaRepository.findById(saved.getCourseId())
            .map(mapper::toCourseDomain)
            .orElseThrow(() -> new RuntimeException("Failed to save course"));
    }

    @Override
    public Optional<Course> findById(Long courseId) {
        return jpaRepository.findById(courseId)
            .map(mapper::toCourseDomain);
    }

    @Override
    public List<Course> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toCourseDomain)
            .toList();
    }
}
