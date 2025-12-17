package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking response model")
public class BookingResponse {
    @Schema(description = "Booking ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID", example = "1")
    private Long userId;
    
    @Schema(description = "Amenity ID", example = "GYM")
    private String amenityId;
    
    @Schema(description = "Booking start time", example = "2025-12-01T10:00:00")
    private LocalDateTime startTime;
    
    @Schema(description = "Booking end time", example = "2025-12-01T11:00:00")
    private LocalDateTime endTime;
    
    @Schema(description = "Booking status", example = "CONFIRMED")
    private String status;
    
    public BookingResponse(Long id, Long userId, String amenityId, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.userId = userId;
        this.amenityId = amenityId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
