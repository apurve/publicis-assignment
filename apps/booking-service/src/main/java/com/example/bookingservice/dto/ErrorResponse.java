package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response model")
public record ErrorResponse(
    @Schema(description = "HTTP status code", example = "400")
    int status,
    
    @Schema(description = "Error message", example = "Maintenance fee not paid")
    String message,
    
    @Schema(description = "Timestamp of the error", example = "1701360000000")
    long timestamp
) {
}
