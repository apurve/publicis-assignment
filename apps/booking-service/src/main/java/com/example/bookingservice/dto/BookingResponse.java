package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking response model")
public record BookingResponse(
    @Schema(description = "Booking ID", example = "1")
    Long id,
    
    @Schema(description = "User ID", example = "1")
    Long userId,
    
    @Schema(description = "Amenity ID", example = "GYM")
    String amenityId,
    
    @Schema(description = "Booking start time", example = "2025-12-01T10:00:00")
    LocalDateTime startTime,
    
    @Schema(description = "Booking end time", example = "2025-12-01T11:00:00")
    LocalDateTime endTime,
    
    @Schema(description = "Booking status", example = "CONFIRMED")
    String status
) {
}
