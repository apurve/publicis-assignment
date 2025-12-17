package com.example.notificationservice.dto;

import java.time.LocalDateTime;

/**
 * DTO for booking events from Kafka
 * Maps to the BookingRequest structure from catalog-service
 */
public class BookingEventDto {
    
    private Long userId;
    private String serviceId;
    private String serviceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Constructors
    public BookingEventDto() {
    }

    public BookingEventDto(Long userId, String serviceId, String serviceType,
                          LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "BookingEventDto{" +
                "userId=" + userId +
                ", serviceId='" + serviceId + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
