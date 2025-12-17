package com.example.catalogservice.serializer;

import com.example.catalogservice.dto.BookingRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BookingRequestSerializer implements Serializer<BookingRequestDto> {
    private static final Logger logger = LoggerFactory.getLogger(BookingRequestSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookingRequestSerializer() {
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, BookingRequestDto data) {
        try {
            if (data == null) {
                logger.warn("Null data received for serialization");
                return null;
            }
            logger.debug("Serializing BookingRequestDto: {}", data);
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error("Error serializing BookingRequestDto: {}", e.getMessage());
            throw new RuntimeException("Error serializing BookingRequestDto", e);
        }
    }

    @Override
    public void close() {
    }
}
