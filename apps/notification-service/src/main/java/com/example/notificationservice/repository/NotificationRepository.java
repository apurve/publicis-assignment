package com.example.notificationservice.repository;

import com.example.notificationservice.model.Notification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive Repository using R2DBC
 * 
 * LEARNING NOTE: Unlike JPA repositories that return List/Optional,
 * R2DBC repositories return Mono (0-1 results) or Flux (0-N results)
 * 
 * Key Reactive Types:
 * - Mono<T>: Asynchronous 0-1 result (like Optional)
 * - Flux<T>: Asynchronous 0-N results (like Stream/List)
 */
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
    
    /**
     * Find all notifications for a user, ordered by creation date (newest first)
     * 
     * @return Flux<Notification> - Stream of notifications
     */
    Flux<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find unread notifications for a user
     * 
     * @return Flux<Notification> - Stream of unread notifications
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = false ORDER BY created_at DESC")
    Flux<Notification> findUnreadByUserId(Long userId);
    
    /**
     * Count unread notifications for a user
     * 
     * @return Mono<Long> - Count wrapped in Mono
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = false")
    Mono<Long> countUnreadByUserId(Long userId);
    
    /**
     * Find notifications by user and read status
     */
    Flux<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
}
