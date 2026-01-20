package com.example.catalogservice.service;

import com.example.catalogservice.dto.ServiceDetailDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogDataServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CatalogDataService catalogDataService;

    @Test
    void getAllServices_CacheHit() throws JsonProcessingException {
        // Arrange
        String cachedData = "[{\"id\":\"GYM\"}]";
        List<ServiceDetailDto> expectedServices = Collections.singletonList(
                new ServiceDetailDto("GYM", "Gym", "Desc", "AMENITY", new ArrayList<>())
        );

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("catalog:services")).thenReturn(cachedData);
        when(objectMapper.readValue(eq(cachedData), any(TypeReference.class))).thenReturn(expectedServices);

        // Act
        List<ServiceDetailDto> result = catalogDataService.getAllServices();

        // Assert
        assertEquals(1, result.size());
        assertEquals("GYM", result.get(0).id());
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get("catalog:services");
    }

    @Test
    void getAllServices_CacheMiss() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("catalog:services")).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn("json");

        // Act
        List<ServiceDetailDto> result = catalogDataService.getAllServices();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("GYM", result.get(0).id()); // Mock data first item
        verify(valueOperations, times(1)).set(eq("catalog:services"), anyString(), anyLong(), any());
    }
}
