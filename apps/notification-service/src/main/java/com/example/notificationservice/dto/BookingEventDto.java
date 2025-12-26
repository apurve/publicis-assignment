package com.example.notificationservice.dto;

import java.time.LocalDateTime;

/**
 * DTO for booking events from Kafka
 * Maps to the BookingRequest structure from catalog-service
 */
public record BookingEventDto(
    Long userId,
    String serviceId,
    String serviceType,
    LocalDateTime startTime,
    LocalDateTime endTime
) {
}
