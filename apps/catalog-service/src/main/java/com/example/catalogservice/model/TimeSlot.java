package com.example.catalogservice.model;

import java.time.LocalDateTime;

public class TimeSlot {
    private String slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;
    
    public TimeSlot() {
    }
    
    public TimeSlot(String slotId, LocalDateTime startTime, LocalDateTime endTime, boolean available) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
