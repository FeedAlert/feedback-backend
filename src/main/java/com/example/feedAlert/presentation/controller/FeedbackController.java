package com.example.feedAlert.presentation.controller;

import com.example.feedAlert.application.dto.CreateFeedbackRequest;
import com.example.feedAlert.application.dto.FeedbackResponse;
import com.example.feedAlert.application.usecase.CreateFeedbackUseCase;
import com.example.feedAlert.application.usecase.GetFeedbackUseCase;
import com.example.feedAlert.domain.gateway.UserRepository;
import com.example.feedAlert.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    
    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final GetFeedbackUseCase getFeedbackUseCase;
    private final UserRepository userRepository;

    public FeedbackController(CreateFeedbackUseCase createFeedbackUseCase, 
                             GetFeedbackUseCase getFeedbackUseCase,
                             UserRepository userRepository) {
        this.createFeedbackUseCase = createFeedbackUseCase;
        this.getFeedbackUseCase = getFeedbackUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @Valid @RequestBody CreateFeedbackRequest request,
            Authentication authentication) {
        
        // Extrair email do usuário autenticado
        String userEmail = authentication.getName();
        
        // Buscar userId pelo email
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        
        FeedbackResponse response = createFeedbackUseCase.execute(request, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @feedbackController.canAccessFeedback(authentication, #id)")
    public ResponseEntity<FeedbackResponse> getFeedback(
            @PathVariable Long id,
            Authentication authentication) {
        
        FeedbackResponse response = getFeedbackUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean urgent,
            Authentication authentication) {
        
        // Apenas admins podem listar todos os feedbacks
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

    /**
     * Método auxiliar para verificar se o usuário pode acessar um feedback específico
     * Usado no @PreAuthorize - permite que o usuário veja apenas seus próprios feedbacks
     */
    public boolean canAccessFeedback(Authentication authentication, Long feedbackId) {
        try {
            if (authentication == null || feedbackId == null) {
                return false;
            }
            
            String userEmail = authentication.getName();
            if (userEmail == null || userEmail.isEmpty()) {
                return false;
            }
            
            // Buscar usuário pelo email
            User user = userRepository.findByEmail(userEmail)
                .orElse(null);
            
            if (user == null) {
                return false;
            }
            
            // Buscar feedback - se não existir, retorna false
            FeedbackResponse feedback;
            try {
                feedback = getFeedbackUseCase.findById(feedbackId);
            } catch (IllegalArgumentException e) {
                // Feedback não encontrado
                return false;
            }
            
            if (feedback == null || feedback.student() == null) {
                return false;
            }
            
            // Verificar se o feedback pertence ao usuário
            Long feedbackUserId = feedback.student().userId();
            if (feedbackUserId == null) {
                return false;
            }
            
            return feedbackUserId.equals(user.getUserId());
        } catch (Exception e) {
            // Em caso de qualquer erro, negar acesso
            return false;
        }
    }
}
