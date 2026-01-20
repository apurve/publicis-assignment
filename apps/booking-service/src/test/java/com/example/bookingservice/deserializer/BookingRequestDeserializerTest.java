package com.example.bookingservice.deserializer;

import com.example.bookingservice.dto.BookingRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestDeserializerTest {

    private final BookingRequestDeserializer deserializer = new BookingRequestDeserializer();

    @Test
    void deserialize_Success() {
        // Arrange
        String json = "{\"userId\":1,\"amenityId\":\"gym\",\"startTime\":\"2025-12-01T10:00:00\",\"endTime\":\"2025-12-01T11:00:00\"}";
        byte[] data = json.getBytes();

        // Act
        BookingRequest result = deserializer.deserialize("topic", data);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals("gym", result.amenityId());
        assertEquals(LocalDateTime.of(2025, 12, 1, 10, 0), result.startTime());
        assertEquals(LocalDateTime.of(2025, 12, 1, 11, 0), result.endTime());
    }

    @Test
    void deserialize_NullData() {
        // Act
        BookingRequest result = deserializer.deserialize("topic", null);

        // Assert
        assertNull(result);
    }
}
