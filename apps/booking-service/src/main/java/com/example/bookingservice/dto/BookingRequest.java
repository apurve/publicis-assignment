package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking request model")
public class BookingRequest {
    @Schema(description = "User ID", example = "1", required = true)
    private Long userId;
    
    @Schema(description = "Amenity ID", example = "GYM", required = true)
    @com.fasterxml.jackson.annotation.JsonAlias("serviceId")
    private String amenityId;
    
    @Schema(description = "Booking start time", example = "2025-12-01T10:00:00", required = true)
    private LocalDateTime startTime;
    
    @Schema(description = "Booking end time", example = "2025-12-01T11:00:00", required = true)
    private LocalDateTime endTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(String amenityId) {
        this.amenityId = amenityId;
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
        return "BookingRequest{" +
                "userId=" + userId +
                ", amenityId='" + amenityId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
