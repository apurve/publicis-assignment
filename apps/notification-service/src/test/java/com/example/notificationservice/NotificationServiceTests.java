package com.example.notificationservice;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reactive Tests using StepVerifier
 * 
 * LEARNING NOTE: Testing reactive code requires StepVerifier
 * from reactor-test dependency. It allows testing async flows.
 * 
 * We use @DataR2dbcTest instead of @SpringBootTest to only load
 * R2DBC components, avoiding Kafka dependencies in tests.
 */
@DataR2dbcTest
@ActiveProfiles("test")
class NotificationServiceTests {

    @Autowired
    private NotificationRepository repository;

    /**
     * Test reactive repository save
     * 
     * StepVerifier verifies each step of the reactive pipeline
     */
    @Test
    void testSaveNotification() {
        Notification notification = Notification.create(
            1L,
            "Test Notification",
            "Test Message",
            com.example.notificationservice.model.NotificationType.BOOKING_CONFIRMED,
            com.example.notificationservice.model.NotificationChannel.IN_APP
        );

        Mono<Notification> savedMono = repository.save(notification);

        // StepVerifier tests the reactive stream
        StepVerifier.create(savedMono)
                .assertNext(saved -> {
                    assertThat(saved.id()).isNotNull();
                    assertThat(saved.title()).isEqualTo("Test Notification");
                    assertThat(saved.userId()).isEqualTo(1L);
                })
                .verifyComplete();
    }
}
