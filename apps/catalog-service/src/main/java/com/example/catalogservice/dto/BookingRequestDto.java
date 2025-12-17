package com.example.catalogservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking request from catalog")
public class BookingRequestDto {
    @Schema(description = "User ID", example = "1", required = true)
    private Long userId;
    
    @Schema(description = "Service ID", example = "GYM", required = true)
    private String serviceId;
    
    @Schema(description = "Service Type", example = "AMENITY", required = true)
    private String serviceType;
    
    @Schema(description = "Start time", example = "2025-12-01T10:00:00", required = true)
    private LocalDateTime startTime;
    
    @Schema(description = "End time", example = "2025-12-01T11:00:00", required = true)
    private LocalDateTime endTime;

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
        return "BookingRequestDto{" +
                "userId=" + userId +
                ", serviceId='" + serviceId + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
