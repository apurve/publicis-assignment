package com.example.notificationservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Reactive Notification Service using Spring WebFlux
 * 
 * This service demonstrates reactive programming patterns:
 * - WebFlux for non-blocking REST APIs
 * - R2DBC for reactive database access
 * - Reactor Kafka for event streaming
 * - Server-Sent Events (SSE) for real-time notifications
 */
@SpringBootApplication

@OpenAPIDefinition(
    info = @Info(
        title = "Notification Service API (Reactive)",
        version = "1.0",
        description = "Reactive notification service using Spring WebFlux, R2DBC, and Reactor Kafka"
    )
)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
