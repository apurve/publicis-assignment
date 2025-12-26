package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.stream.NotificationStreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reactive REST Controller using WebFlux
 * 
 * LEARNING NOTES - WebFlux vs Spring MVC:
 * 
 * Spring MVC (Blocking):
 * - Returns List, Object, ResponseEntity
 * - Blocks thread waiting for database/network
 * - Limited by thread pool size
 * 
 * Spring WebFlux (Reactive):
 * - Returns Mono<T>, Flux<T>
 * - Non-blocking, event-driven
 * - Handles much higher concurrency
 * - Supports Server-Sent Events (SSE)
 * 
 * KEY DIFFERENCE:
 * - MVC: @GetMapping returns List<Notification>
 * - WebFlux: @GetMapping returns Mono<List<NotificationDto>> or Flux<NotificationDto>
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications (Reactive)", description = "Reactive notification endpoints using WebFlux")
public class NotificationController {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;
    private final NotificationStreamService streamService;
    
    public NotificationController(NotificationService notificationService,
                                 NotificationStreamService streamService) {
        this.notificationService = notificationService;
        this.streamService = streamService;
    }
    
    /**
     * Get all notifications for a user
     * 
     * REACTIVE PATTERN: Flux → List conversion
     * - collectList() transforms Flux<Notification> to Mono<List<Notification>>
     * - map() transforms entities to DTOs
     */
    @Operation(summary = "Get all notifications for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = NotificationDto.class)))
    })
    @GetMapping("/user/{userId}")
    public Mono<List<NotificationDto>> getUserNotifications(@PathVariable Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        
        return notificationService.getUserNotifications(userId)
                .map(notificationService::toDto)
                .collectList();
    }
    
    /**
     * Get unread notifications
     */
    @Operation(summary = "Get unread notifications for a user")
    @GetMapping("/user/{userId}/unread")
    public Mono<List<NotificationDto>> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId)
                .map(notificationService::toDto)
                .collectList();
    }
    
    /**
     * Get unread count
     */
    @Operation(summary = "Get unread notification count")
    @GetMapping("/user/{userId}/unread-count")
    public Mono<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        return notificationService.getUnreadCount(userId)
                .map(count -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("userId", userId);
                    response.put("unreadCount", count);
                    return response;
                });
    }
    
    /**
     * Mark notification as read
     * 
     * REACTIVE PATTERN: PATCH for partial update
     */
    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public Mono<NotificationDto> markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id)
                .map(notificationService::toDto);
    }
    
    /**
     * Stream notifications using Server-Sent Events (SSE)
     * 
     * 🌟 KEY REACTIVE FEATURE: Server-Sent Events
     * 
     * LEARNING NOTES:
     * - produces = MediaType.TEXT_EVENT_STREAM_VALUE enables SSE
     * - Returns Flux<T> for continuous stream
     * - Browser opens persistent HTTP connection
     * - Server pushes events as they occur
     * - Client receives real-time updates
     * 
     * Test with curl:
     * curl -N http://localhost:8082/api/notifications/stream/user/1
     * 
     * Test in browser:
     * const eventSource = new EventSource('/api/notifications/stream/user/1');
     * eventSource.onmessage = (event) => console.log(event.data);
     */
    @Operation(summary = "Stream notifications in real-time (SSE)",
            description = "Server-Sent Events endpoint for receiving real-time notifications")
    @GetMapping(value = "/stream/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationDto> streamNotifications(@PathVariable Long userId) {
        log.info("Opening SSE stream for user: {}", userId);
        
        return streamService.getNotificationStream(userId)
                .map(notificationService::toDto)
                .doOnSubscribe(sub -> log.info("SSE subscription started for user: {}", userId))
                .doOnCancel(() -> log.info("SSE subscription cancelled for user: {}", userId))
                .doOnComplete(() -> log.info("SSE stream completed for user: {}", userId));
    }
    
    /**
     * Stream notifications with ServerSentEvent wrapper
     * 
     * Advanced SSE with event IDs and names for better client handling
     */
    @GetMapping(value = "/stream-sse/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationDto>> streamNotificationsWithSSE(@PathVariable Long userId) {
        return streamService.getNotificationStream(userId)
                .map(notification -> ServerSentEvent.<NotificationDto>builder()
                        .id(String.valueOf(notification.id()))
                        .event("notification")
                        .data(notificationService.toDto(notification))
                        .build());
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "notification-service");
        status.put("type", "reactive");
        return Mono.just(status);
    }
}
