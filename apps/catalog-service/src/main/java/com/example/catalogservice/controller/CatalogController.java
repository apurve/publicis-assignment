package com.example.catalogservice.controller;

import com.example.catalogservice.dto.BookingRequestDto;
import com.example.catalogservice.dto.ServiceDetailDto;
import com.example.catalogservice.service.CatalogDataService;

import com.example.catalogservice.producer.BookingProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Service Catalog", description = "Browse services and available time slots")
public class CatalogController {
    
    private final BookingProducer bookingProducer;
    private final CatalogDataService catalogDataService;
    
    public CatalogController(BookingProducer bookingProducer, CatalogDataService catalogDataService) {
        this.bookingProducer = bookingProducer;
        this.catalogDataService = catalogDataService;
    }
    
    @Operation(
            summary = "Get all available services",
            description = "Returns list of all available services in the apartment complex (amenities and repairs)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved services",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceDetailDto.class)
                    )
            )
    })
    @GetMapping("/services")
    public ResponseEntity<List<ServiceDetailDto>> getServices() {
        return ResponseEntity.ok(catalogDataService.getAllServices());
    }
    
    @Operation(
            summary = "Request booking for a service",
            description = "Initiates a booking request which will be processed asynchronously via Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Booking request accepted and queued for processing",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid booking request",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/bookings")
    public ResponseEntity<String> requestBooking(@RequestBody BookingRequestDto request) {
        bookingProducer.sendBookingRequest(request);
        return ResponseEntity.accepted().body("{\"message\": \"Booking request submitted\", \"status\": \"PENDING\"}");
    }
}
