package com.example.notificationservice.stream;

import com.example.notificationservice.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

/**
 * Notification Stream Service for Server-Sent Events (SSE)
 * 
 * LEARNING NOTES - Hot vs Cold Publishers:
 * 
 * COLD Publisher (most Flux/Mono):
 * - Starts producing data when subscribed
 * - Each subscriber gets its own independent stream
 * - Example: Flux.range(1, 10)
 * 
 * HOT Publisher (Sinks.many().multicast()):
 * - Produces data regardless of subscribers
 * - All subscribers share the same stream
 * - Late subscribers miss earlier events
 * - Use case: Real-time notifications, chat messages, live updates
 * 
 * This implementation uses HOT publisher for real-time notification broadcasting
 */
@Service
public class NotificationStreamService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationStreamService.class);
    
    /**
     * Hot publisher for broadcasting notifications
     * 
     * Sinks.many().multicast():
     * - Multiple subscribers
     * - Shared stream
     * - onBackpressureBuffer() handles slow consumers
     */
    private final Sinks.Many<Notification> notificationSink;
    private final Flux<Notification> notificationFlux;
    
    public NotificationStreamService() {
        // Create a multicast sink for hot observable
        this.notificationSink = Sinks.many().multicast().onBackpressureBuffer();
        
        // Create a hot Flux that multiple subscribers can share
        this.notificationFlux = notificationSink.asFlux();
        
        log.info("✅ Notification Stream Service initialized (HOT publisher)");
    }
    
    /**
     * Emit a notification to all subscribers
     * 
     * @param notification The notification to broadcast
     */
    public void emitNotification(Notification notification) {
        Sinks.EmitResult result = notificationSink.tryEmitNext(notification);
        
        if (result.isSuccess()) {
            log.info("📡 Broadcast notification {} to all subscribers", notification.getId());
        } else {
            log.warn("Failed to emit notification: {}", result);
        }
    }
    
    /**
     * Get notification stream for a specific user
     * 
     * REACTIVE PATTERN: Filter infinite stream for specific user
     * 
     * SSE clients will subscribe to this Flux and receive real-time updates
     */
    public Flux<Notification> getNotificationStream(Long userId) {
        log.info("New SSE subscriber for user: {}", userId);
        
        return notificationFlux
                .filter(notification -> notification.getUserId().equals(userId))
                .doOnSubscribe(sub -> log.info("User {} subscribed to notification stream", userId))
                .doOnCancel(() -> log.info("User {} unsubscribed from notification stream", userId));
    }
    
    /**
     * Get notification stream with heartbeat
     * 
     * Sends periodic heartbeat to keep SSE connection alive
     * Merges real notifications with empty heartbeat notifications
     */
    public Flux<Notification> getNotificationStreamWithHeartbeat(Long userId) {
        // Real notifications for user
        Flux<Notification> userNotifications = getNotificationStream(userId);
        
        // Heartbeat every 30 seconds (empty notification as keep-alive)
        Flux<Notification> heartbeat = Flux.interval(Duration.ofSeconds(30))
                .map(tick -> {
                    Notification heartbeatNotif = new Notification();
                    heartbeatNotif.setUserId(userId);
                    heartbeatNotif.setTitle("heartbeat");
                    return heartbeatNotif;
                });
        
        // Merge both streams
        return Flux.merge(userNotifications, heartbeat);
    }
}
