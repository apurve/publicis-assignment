package com.example.bookingservice.deserializer;

import com.example.bookingservice.dto.BookingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BookingRequestDeserializer implements Deserializer<BookingRequest> {
    private static final Logger logger = LoggerFactory.getLogger(BookingRequestDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookingRequestDeserializer() {
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public BookingRequest deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                logger.warn("Null data received for deserialization");
                return null;
            }
            logger.debug("Deserializing BookingRequest from topic: {}", topic);
            return objectMapper.readValue(data, BookingRequest.class);
        } catch (Exception e) {
            logger.error("Error deserializing BookingRequest: {}", e.getMessage());
            throw new RuntimeException("Error deserializing BookingRequest", e);
        }
    }

    @Override
    public void close() {
    }
}
