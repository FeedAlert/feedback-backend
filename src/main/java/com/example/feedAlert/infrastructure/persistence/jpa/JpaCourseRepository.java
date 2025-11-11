package com.example.feedAlert.infrastructure.persistence.jpa;

import com.example.feedAlert.infrastructure.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCourseRepository extends JpaRepository<CourseEntity, Long> {
}

