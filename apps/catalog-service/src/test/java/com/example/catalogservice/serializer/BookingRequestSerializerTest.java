package com.example.catalogservice.serializer;

import com.example.catalogservice.dto.BookingRequestDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestSerializerTest {

    private final BookingRequestSerializer serializer = new BookingRequestSerializer();

    @Test
    void serialize_Success() {
        // Arrange
        BookingRequestDto dto = new BookingRequestDto(
                1L, "GYM", "AMENITY",
                LocalDateTime.of(2025, 12, 1, 10, 0),
                LocalDateTime.of(2025, 12, 1, 11, 0)
        );

        // Act
        byte[] result = serializer.serialize("topic", dto);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String json = new String(result);
        assertTrue(json.contains("\"userId\":1"));
        assertTrue(json.contains("\"serviceId\":\"GYM\""));
    }

    @Test
    void serialize_NullData() {
        // Act
        byte[] result = serializer.serialize("topic", null);

        // Assert
        assertNull(result);
    }
}
