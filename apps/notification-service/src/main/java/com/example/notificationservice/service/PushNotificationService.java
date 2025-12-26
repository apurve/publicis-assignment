package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Push Notification Service (Mock Implementation)
 * 
 * LEARNING NOTE: In production, this would use WebClient to call
 * Firebase Cloud Messaging (FCM) or Apple Push Notification Service (APNS).
 * 
 * Example with WebClient:
 * return webClient.post()
 *     .uri("/fcm/send")
 *     .bodyValue(fcmPayload)
 *     .retrieve()
 *     .bodyToMono(FcmResponse.class)
 *     .timeout(Duration.ofSeconds(5))
 *     .retry(3)
 *     .then();
 */
@Service
public class PushNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);
    
    /**
     * Send push notification (mock implementation)
     * 
     * REACTIVE PATTERN: Mono<Void> for async completion
     */
    public Mono<Void> sendPushNotification(Notification notification) {
        return Mono.delay(Duration.ofMillis(100)) // Simulate API call delay
                .doOnNext(i -> log.info("📱 [PUSH] Sent to user {}: {} - {}", 
                        notification.userId(),
                        notification.title(),
                        notification.message()))
                .then();
    }
}
