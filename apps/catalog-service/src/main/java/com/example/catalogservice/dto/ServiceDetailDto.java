package com.example.catalogservice.dto;

import com.example.catalogservice.model.TimeSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Service details with available slots")
public record ServiceDetailDto(
    @Schema(description = "Service ID")
    String id,
    
    @Schema(description = "Service name")
    String name,
    
    @Schema(description = "Service description")
    String description,
    
    @Schema(description = "Service type (AMENITY/REPAIR)")
    String type,
    
    @Schema(description = "Available time slots")
    List<TimeSlot> availableSlots
) {
}
