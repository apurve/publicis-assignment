package com.example.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reactive Kafka Consumer Configuration using Reactor Kafka
 * 
 * LEARNING NOTE: Unlike traditional Spring Kafka (@KafkaListener),
 * Reactor Kafka uses KafkaReceiver which returns Flux<ReceiverRecord>
 * for fully reactive, non-blocking consumption
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topics.booking-requests}")
    private String bookingRequestsTopic;

    /**
     * Reactive Kafka Receiver Options
     * 
     * ReceiverOptions is used to configure the reactive Kafka consumer
     */
    @Bean
    public ReceiverOptions<String, String> receiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        
        return ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton(bookingRequestsTopic));
    }

    /**
     * Kafka Receiver Bean
     * 
     * KafkaReceiver provides the reactive stream of messages
     * @return KafkaReceiver<String, String>
     */
    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> receiverOptions) {
        return KafkaReceiver.create(receiverOptions);
    }
}
