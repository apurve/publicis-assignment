package com.example.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response model")
public class ErrorResponse {
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error message", example = "Maintenance fee not paid")
    private String message;
    
    @Schema(description = "Timestamp of the error", example = "1701360000000")
    private long timestamp;
    
    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
