package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Email Notification Service (Mock Implementation)
 * 
 * LEARNING NOTE: This demonstrates WebClient usage for non-blocking HTTP calls.
 * In production, this would call SendGrid, AWS SES, or similar email service.
 */
@Service
public class EmailNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    
    /**
     * Send email notification (mock implementation)
     * 
     * REACTIVE PATTERN: Returns Mono<Void> for async completion signal
     */
    public Mono<Void> sendEmail(Notification notification) {
        return Mono.delay(Duration.ofMillis(100)) // Simulate network delay
                .doOnNext(i -> log.info("📧 [EMAIL] Sent to user {}: {} - {}", 
                        notification.userId(),
                        notification.title(),
                        notification.message()))
                .then(); // Convert to Mono<Void>
    }
}
