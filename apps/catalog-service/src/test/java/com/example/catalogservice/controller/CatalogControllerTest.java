package com.example.catalogservice.controller;

import com.example.catalogservice.dto.BookingRequestDto;
import com.example.catalogservice.dto.ServiceDetailDto;
import com.example.catalogservice.producer.BookingProducer;
import com.example.catalogservice.service.CatalogDataService;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingProducer bookingProducer;

    @Mock
    private CatalogDataService catalogDataService;

    @InjectMocks
    private CatalogController catalogController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getServices_Success() throws Exception {
        // Arrange
        List<ServiceDetailDto> services = new ArrayList<>();
        services.add(new ServiceDetailDto("GYM", "Gym", "Desc", "AMENITY", new ArrayList<>()));
        when(catalogDataService.getAllServices()).thenReturn(services);

        // Act & Assert
        mockMvc.perform(get("/api/catalog/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("GYM"));
    }

    @Test
    void requestBooking_Success() throws Exception {
        // Arrange
        BookingRequestDto request = new BookingRequestDto(
                1L, "GYM", "AMENITY",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1)
        );

        // Act & Assert
        mockMvc.perform(post("/api/catalog/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(bookingProducer).sendBookingRequest(any(BookingRequestDto.class));
    }
}
