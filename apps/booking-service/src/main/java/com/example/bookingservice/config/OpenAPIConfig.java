package com.example.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI bookingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking & Reservation Management API")
                        .description("API for managing apartment amenity bookings")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Apartment Management System")
                                .email("support@apartments.com")));
    }
}
