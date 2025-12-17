package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.BookingEventDto;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

/**
 * Reactive Kafka Consumer using Reactor Kafka
 * 
 * LEARNING NOTES - Key Reactive Concepts:
 * 
 * 1. KafkaReceiver.receive() returns Flux<ReceiverRecord> - infinite stream
 * 2. @PostConstruct starts consumption automatically on startup
 * 3. flatMap() for async processing of each message
 * 4. subscribe() activates the reactive stream (lazy execution)
 * 5. Error handling with doOnError() and onErrorContinue()
 * 
 * Compare to blocking @KafkaListener:
 * - Traditional: @KafkaListener blocks thread per message
 * - Reactive: Handles messages on event loop, non-blocking
 */
@Component
public class BookingEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);
    
    private final KafkaReceiver<String, String> kafkaReceiver;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    public BookingEventConsumer(KafkaReceiver<String, String> kafkaReceiver,
                               NotificationService notificationService,
                               ObjectMapper objectMapper) {
        this.kafkaReceiver = kafkaReceiver;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Start consuming Kafka messages on application startup
     * 
     * REACTIVE PATTERN: Flux processing pipeline
     */
    @PostConstruct
    public void startConsuming() {
        log.info("Starting reactive Kafka consumer for booking events");
        
        kafkaReceiver.receive()
                // Log each received record
                .doOnNext(record -> log.info("Received Kafka message: key={}, partition={}, offset={}", 
                        record.key(), record.partition(), record.offset()))
                
                // Process each message reactively
                .flatMap(this::processBookingEvent)
                
                // Error handling - continue on error (don't stop stream)
                .doOnError(error -> log.error("Error processing Kafka message", error))
                .onErrorContinue((throwable, obj) -> 
                        log.error("Continuing after error: {}", throwable.getMessage()))
                
                // Subscribe to activate the stream
                .subscribe(
                        notification -> log.info("Successfully processed notification: {}", notification),
                        error -> log.error("Fatal error in Kafka consumer", error),
                        () -> log.warn("Kafka consumer stream completed (unexpected)")
                );
    }
    
    /**
     * Process a single booking event message
     * 
     * REACTIVE PATTERN: 
     * - Returns Mono<Notification> for async processing
     * - flatMap chains the database save operation
     * - Entire pipeline is non-blocking
     */
    private reactor.core.publisher.Mono<Notification> processBookingEvent(ReceiverRecord<String, String> record) {
        try {
            // Deserialize JSON to BookingEventDto
            String jsonValue = record.value();
            BookingEventDto bookingEvent = objectMapper.readValue(jsonValue, BookingEventDto.class);
            
            log.info("Processing booking event: {}", bookingEvent);
            
            // Create notification from booking event
            Notification notification = createNotificationFromBooking(bookingEvent);
            
            // Send notification (returns Mono<Notification>)
            return notificationService.createAndSendNotification(notification)
                    .doOnSuccess(n -> {
                        // Acknowledge Kafka message after successful processing
                        record.receiverOffset().acknowledge();
                        log.info("Acknowledged Kafka offset: {}", record.offset());
                    });
            
        } catch (Exception e) {
            log.error("Error parsing booking event", e);
            return reactor.core.publisher.Mono.empty();
        }
    }
    
    /**
     * Transform booking event to notification
     */
    private Notification createNotificationFromBooking(BookingEventDto bookingEvent) {
        String title = "Booking Confirmation";
        String message = String.format(
                "Your booking for %s has been confirmed for %s to %s",
                bookingEvent.getServiceId(),
                bookingEvent.getStartTime(),
                bookingEvent.getEndTime()
        );
        
        return new Notification(
                bookingEvent.getUserId(),
                title,
                message,
                NotificationType.BOOKING_CONFIRMED,
                NotificationChannel.IN_APP
        );
    }
}
