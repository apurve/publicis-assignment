package com.example.bookingservice.controller;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        String amenityId = "gym";
        LocalDateTime startTime = LocalDateTime.of(2025, 12, 1, 10, 0);
        LocalDateTime endTime = startTime.plusHours(1);

        BookingRequest request = new BookingRequest(userId, amenityId, startTime, endTime);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setUserId(userId);
        booking.setAmenityId(amenityId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("CONFIRMED");

        when(bookingService.createBooking(eq(userId), eq(amenityId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void createBooking_Failure() throws Exception {
        // Arrange
        Long userId = 1L;
        String amenityId = "gym";
        LocalDateTime startTime = LocalDateTime.of(2025, 12, 1, 10, 0);
        LocalDateTime endTime = startTime.plusHours(1);

        BookingRequest request = new BookingRequest(userId, amenityId, startTime, endTime);

        when(bookingService.createBooking(eq(userId), eq(amenityId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
