package com.example.feedAlert.presentation.controller;

import com.example.feedAlert.application.dto.CourseResponse;
import com.example.feedAlert.application.usecase.GetCourseUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);
    
    private final GetCourseUseCase getCourseUseCase;

    public CourseController(GetCourseUseCase getCourseUseCase) {
        this.getCourseUseCase = getCourseUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        CourseResponse response = getCourseUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> responses = getCourseUseCase.findAll();
        return ResponseEntity.ok(responses);
    }
}
