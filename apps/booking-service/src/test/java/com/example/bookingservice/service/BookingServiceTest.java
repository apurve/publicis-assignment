package com.example.bookingservice.service;

import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_Success() {
        // Arrange
        Long userId = 1L;
        String amenityId = "gym";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        Booking savedBooking = new Booking();
        savedBooking.setId(100L);
        savedBooking.setUserId(userId);
        savedBooking.setAmenityId(amenityId);
        savedBooking.setStartTime(startTime);
        savedBooking.setEndTime(endTime);
        savedBooking.setStatus("CONFIRMED");

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Act
        Booking result = bookingService.createBooking(userId, amenityId, startTime, endTime);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("CONFIRMED", result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }
}
