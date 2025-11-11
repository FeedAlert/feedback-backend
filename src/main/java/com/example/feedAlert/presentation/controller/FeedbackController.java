package com.example.feedAlert.presentation.controller;

import com.example.feedAlert.application.dto.CreateFeedbackRequest;
import com.example.feedAlert.application.dto.FeedbackResponse;
import com.example.feedAlert.application.usecase.CreateFeedbackUseCase;
import com.example.feedAlert.application.usecase.GetFeedbackUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final GetFeedbackUseCase getFeedbackUseCase;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @Valid @RequestBody CreateFeedbackRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        // TODO: Extrair userId do token JWT em produção
        if (userId == null) {
            userId = 1L; // Default para desenvolvimento
        }
        
        FeedbackResponse response = createFeedbackUseCase.execute(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedback(@PathVariable Long id) {
        FeedbackResponse response = getFeedbackUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean urgent) {
        
        List<FeedbackResponse> responses;
        
        if (courseId != null) {
            responses = getFeedbackUseCase.findByCourseId(courseId);
        } else if (userId != null) {
            responses = getFeedbackUseCase.findByUserId(userId);
        } else if (urgent != null && urgent) {
            responses = getFeedbackUseCase.findUrgentFeedbacks();
        } else {
            responses = getFeedbackUseCase.findAll();
        }
        
        return ResponseEntity.ok(responses);
    }
}

