package com.example.catalogservice.producer;

import com.example.catalogservice.dto.BookingRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {
    private static final Logger logger = LoggerFactory.getLogger(BookingProducer.class);
    private static final String TOPIC = "booking-requests";
    
    private final KafkaTemplate<String, BookingRequestDto> kafkaTemplate;
    
    public BookingProducer(KafkaTemplate<String, BookingRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendBookingRequest(BookingRequestDto request) {
        logger.info("Sending booking request to Kafka topic '{}': userId={}, serviceId={}", 
            TOPIC, request.getUserId(), request.getServiceId());
        kafkaTemplate.send(TOPIC, request);
        logger.info("Successfully sent booking request to Kafka");
    }
}
