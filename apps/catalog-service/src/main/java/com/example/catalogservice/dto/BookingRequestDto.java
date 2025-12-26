package com.example.catalogservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Booking request from catalog")
public record BookingRequestDto(
    @Schema(description = "User ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId,
    
    @Schema(description = "Service ID", example = "GYM", requiredMode = Schema.RequiredMode.REQUIRED)
    String serviceId,
    
    @Schema(description = "Service Type", example = "AMENITY", requiredMode = Schema.RequiredMode.REQUIRED)
    String serviceType,
    
    @Schema(description = "Start time", example = "2025-12-01T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime startTime,
    
    @Schema(description = "End time", example = "2025-12-01T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime endTime
) {
}
