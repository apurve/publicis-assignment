package com.example.catalogservice.model;

import java.time.LocalDateTime;

public record TimeSlot(
    String slotId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    boolean available
) {
}
