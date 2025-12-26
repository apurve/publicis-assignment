package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.stream.NotificationStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Core Notification Service with Reactive Patterns
 * 
 * LEARNING NOTES:
 * - All methods return Mono<T> or Flux<T>
 * - Operations are lazy (don't execute until .subscribe())
 * - Chaining operations with .map(), .flatMap(), .filter(), etc.
 * - Error handling with .onErrorResume(), .onErrorReturn()
 */
@Service
public class NotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailService;
    private final PushNotificationService pushService;
    private final NotificationStreamService streamService;
    
    public NotificationService(NotificationRepository notificationRepository,
                              EmailNotificationService emailService,
                              PushNotificationService pushService,
                              NotificationStreamService streamService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.pushService = pushService;
        this.streamService = streamService;
    }
    
    /**
     * Create and send notification
     * 
     * REACTIVE PATTERN: flatMap for async composition
     */
    public Mono<Notification> createAndSendNotification(Notification notification) {
        log.info("Creating notification for user: {}", notification.userId());
        
        return notificationRepository.save(notification)
                .flatMap(savedNotification -> {
                    // Send via channels in parallel using Mono.zip
                    Mono<Void> emailSent = sendViaEmail(savedNotification);
                    Mono<Void> pushSent = sendViaPush(savedNotification);
                    
                    return Mono.zip(emailSent, pushSent)
                            .then(Mono.just(savedNotification));
                })
                .map(sent -> sent.withStatus(NotificationStatus.SENT))
                .flatMap(notificationRepository::save)
                .doOnSuccess(n -> {
                    log.info("Notification sent successfully: {}", n.id());
                    // Emit to SSE stream for real-time updates
                    streamService.emitNotification(n);
                })
                .doOnError(e -> log.error("Failed to send notification", e))
                .onErrorResume(e -> Mono.just(notification)); // Fallback on error
    }
    
    /**
     * Send notification via email channel
     */
    private Mono<Void> sendViaEmail(Notification notification) {
        if (notification.channel() == NotificationChannel.EMAIL || 
            notification.channel() == NotificationChannel.IN_APP) {
            return emailService.sendEmail(notification);
        }
        return Mono.empty();
    }
    
    /**
     * Send notification via push channel
     */
    private Mono<Void> sendViaPush(Notification notification) {
        if (notification.channel() == NotificationChannel.PUSH || 
            notification.channel() == NotificationChannel.IN_APP) {
            return pushService.sendPushNotification(notification);
        }
        return Mono.empty();
    }
    
    /**
     * Get all notifications for a user
     * 
     * REACTIVE PATTERN: Flux for streaming results
     */
    public Flux<Notification> getUserNotifications(Long userId) {
        log.debug("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get unread notifications
     */
    public Flux<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    /**
     * Mark notification as read
     * 
     * REACTIVE PATTERN: flatMap to chain async operations
     */
    public Mono<Notification> markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .flatMap(notification -> {
                    Notification updated = notification.withIsRead(true)
                            .withReadAt(LocalDateTime.now())
                            .withStatus(NotificationStatus.READ);
                    return notificationRepository.save(updated);
                })
                .doOnSuccess(n -> log.info("Notification marked as read: {}", notificationId));
    }
    
    /**
     * Get unread count
     */
    public Mono<Long> getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    /**
     * Convert entity to DTO
     */
    public NotificationDto toDto(Notification notification) {
        return new NotificationDto(
            notification.id(),
            notification.userId(),
            notification.title(),
            notification.message(),
            notification.notificationType(),
            notification.channel(),
            notification.status(),
            notification.isRead(),
            notification.createdAt(),
            notification.readAt()
        );
    }
}
