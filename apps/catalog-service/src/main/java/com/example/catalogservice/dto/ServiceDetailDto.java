package com.example.catalogservice.dto;

import com.example.catalogservice.model.TimeSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Service details with available slots")
public class ServiceDetailDto {
    @Schema(description = "Service ID")
    private String id;
    
    @Schema(description = "Service name")
    private String name;
    
    @Schema(description = "Service description")
    private String description;
    
    @Schema(description = "Service type (AMENITY/REPAIR)")
    private String type;
    
    @Schema(description = "Available time slots")
    private List<TimeSlot> availableSlots;
    
    public ServiceDetailDto() {
    }
    
    public ServiceDetailDto(String id, String name, String description, String type, List<TimeSlot> availableSlots) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.availableSlots = availableSlots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TimeSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<TimeSlot> availableSlots) {
        this.availableSlots = availableSlots;
    }
}
