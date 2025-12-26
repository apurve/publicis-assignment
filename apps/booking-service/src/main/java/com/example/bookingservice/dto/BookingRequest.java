package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking request model")
public record BookingRequest(
    @Schema(description = "User ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId,
    
    @Schema(description = "Amenity ID", example = "GYM", requiredMode = Schema.RequiredMode.REQUIRED)
    @com.fasterxml.jackson.annotation.JsonAlias("serviceId")
    String amenityId,
    
    @Schema(description = "Booking start time", example = "2025-12-01T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime startTime,
    
    @Schema(description = "Booking end time", example = "2025-12-01T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime endTime
) {
}
