package com.example.bookingservice.consumer;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BookingConsumer.class);
    
    private final BookingService bookingService;
    
    public BookingConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @KafkaListener(topics = "booking-requests", groupId = "booking-service")
    public void consumeBookingRequest(BookingRequest request) {
        try {
            logger.info("Received booking request from Kafka: {}", request);
            
            logger.info("Processing booking for user: {}, amenity: {}", request.getUserId(), request.getAmenityId());
            bookingService.createBooking(request.getUserId(), request.getAmenityId(), request.getStartTime(), request.getEndTime());
            
            logger.info("Successfully processed booking for user: {}, amenity: {}", request.getUserId(), request.getAmenityId());
        } catch (Exception e) {
            logger.error("Failed to process booking request: {}", e.getMessage(), e);
        }
    }
}
