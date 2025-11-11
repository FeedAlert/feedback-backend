package com.example.feedAlert.domain.gateway;

import com.example.feedAlert.domain.model.Feedback;

public interface PubSubGateway {
    void publishFeedbackEvent(Feedback feedback);
}

