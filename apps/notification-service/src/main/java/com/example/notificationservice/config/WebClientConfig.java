package com.example.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration for non-blocking HTTP calls
 * 
 * LEARNING NOTE: WebClient is the reactive alternative to RestTemplate.
 * It's fully non-blocking and returns Mono/Flux for async operations.
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient bean for making non-blocking HTTP requests
     * to external services (e.g., FCM, SendGrid, etc.)
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080") // Can be configured for external services
                .build();
    }
}
