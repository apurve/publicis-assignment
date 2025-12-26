package com.example.notificationservice.dto;

import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.model.NotificationType;

import java.time.LocalDateTime;

/**
 * Notification DTO for API responses
 */
public record NotificationDto(
    Long id,
    Long userId,
    String title,
    String message,
    NotificationType type,
    NotificationChannel channel,
    NotificationStatus status,
    Boolean isRead,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {
}
