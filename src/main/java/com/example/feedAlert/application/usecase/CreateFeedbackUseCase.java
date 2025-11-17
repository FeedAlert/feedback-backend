package com.example.feedAlert.application.usecase;

import com.example.feedAlert.application.dto.CreateFeedbackRequest;
import com.example.feedAlert.application.dto.FeedbackResponse;
import com.example.feedAlert.application.mapper.FeedbackMapper;
import com.example.feedAlert.domain.gateway.CourseRepository;
import com.example.feedAlert.domain.gateway.FeedbackRepository;
import com.example.feedAlert.domain.gateway.PubSubGateway;
import com.example.feedAlert.domain.gateway.UserRepository;
import com.example.feedAlert.domain.model.Course;
import com.example.feedAlert.domain.model.Feedback;
import com.example.feedAlert.domain.model.User;
import com.example.feedAlert.domain.service.FeedbackDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateFeedbackUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateFeedbackUseCase.class);
    
    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PubSubGateway pubSubGateway;
    private final FeedbackDomainService feedbackDomainService;
    private final FeedbackMapper feedbackMapper;

    public CreateFeedbackUseCase(FeedbackRepository feedbackRepository, CourseRepository courseRepository,
                                 UserRepository userRepository, PubSubGateway pubSubGateway,
                                 FeedbackDomainService feedbackDomainService, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.pubSubGateway = pubSubGateway;
        this.feedbackDomainService = feedbackDomainService;
        this.feedbackMapper = feedbackMapper;
    }

    @Transactional
    public FeedbackResponse execute(CreateFeedbackRequest request, Long userId) {
        log.info("Creating feedback for course {} by user {}", request.courseId(), userId);

        // Buscar curso
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + request.courseId()));

        // Buscar usuário
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Criar feedback
        Feedback feedback = Feedback.builder()
            .course(course)
            .user(user)
            .rating(new Feedback.Rating(request.rating()))
            .comment(request.comment())
            .isUrgent(request.isUrgent() != null && request.isUrgent())
            .createdAt(Instant.now())
            .build();

        // Validar feedback
        feedbackDomainService.validateFeedback(feedback);

        // Salvar feedback
        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback created with ID: {}", savedFeedback.getFeedbackId());

        // Se urgente, publicar evento no Pub/Sub
        if (feedbackDomainService.shouldNotifyAdmins(savedFeedback)) {
            log.info("Feedback is urgent, publishing event to Pub/Sub");
            pubSubGateway.publishFeedbackEvent(savedFeedback);
        }

        return feedbackMapper.toResponse(savedFeedback);
    }
}
