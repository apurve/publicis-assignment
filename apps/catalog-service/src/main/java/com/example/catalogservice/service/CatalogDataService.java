package com.example.catalogservice.service;

import com.example.catalogservice.dto.ServiceDetailDto;
import com.example.catalogservice.model.TimeSlot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CatalogDataService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogDataService.class);
    private static final String CATALOG_KEY = "catalog:services";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CatalogDataService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<ServiceDetailDto> getAllServices() {
        // 1. Try to fetch from Redis
        try {
            String cachedData = redisTemplate.opsForValue().get(CATALOG_KEY);
            if (cachedData != null) {
                logger.info("Fetching services from Redis cache");
                return objectMapper.readValue(cachedData, new TypeReference<List<ServiceDetailDto>>() {});
            }
        } catch (Exception e) {
            logger.error("Error reading from Redis", e);
        }

        // 2. If not in cache, generate data
        logger.info("Generating services (cache miss)");
        List<ServiceDetailDto> services = generateMockData();

        // 3. Store in Redis
        try {
            String json = objectMapper.writeValueAsString(services);
            redisTemplate.opsForValue().set(CATALOG_KEY, json, 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            logger.error("Error writing to Redis", e);
        }

        return services;
    }

    private List<ServiceDetailDto> generateMockData() {
        LocalDateTime now = LocalDateTime.now();
        List<ServiceDetailDto> services = new ArrayList<>();

        // 1. Gym
        List<TimeSlot> gymSlots = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            gymSlots.add(new TimeSlot("GYM-SLOT-" + i + "-AM", 
                    now.plusDays(i).withHour(6).withMinute(0), 
                    now.plusDays(i).withHour(7).withMinute(0), true));
            gymSlots.add(new TimeSlot("GYM-SLOT-" + i + "-PM", 
                    now.plusDays(i).withHour(18).withMinute(0), 
                    now.plusDays(i).withHour(19).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("GYM", "Gym Session", "Book a 1-hour gym session", "AMENITY", gymSlots));

        // 2. Swimming Pool
        List<TimeSlot> poolSlots = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            poolSlots.add(new TimeSlot("POOL-SLOT-" + i + "-AM", 
                    now.plusDays(i).withHour(7).withMinute(0), 
                    now.plusDays(i).withHour(8).withMinute(0), true));
            poolSlots.add(new TimeSlot("POOL-SLOT-" + i + "-PM", 
                    now.plusDays(i).withHour(17).withMinute(0), 
                    now.plusDays(i).withHour(18).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("POOL", "Swimming Pool", "Book a 1-hour swimming session", "AMENITY", poolSlots));

        // 3. Tennis Court
        List<TimeSlot> tennisSlots = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
             tennisSlots.add(new TimeSlot("TENNIS-SLOT-" + i, 
                    now.plusDays(i).withHour(16).withMinute(0), 
                    now.plusDays(i).withHour(17).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("TENNIS", "Tennis Court", "Book the tennis court", "AMENITY", tennisSlots));
        
        // 4. Party Hall
        List<TimeSlot> hallSlots = Arrays.asList(
            new TimeSlot("HALL-SLOT-1", now.plusDays(5).withHour(18).withMinute(0), now.plusDays(5).withHour(22).withMinute(0), true)
        );
        services.add(new ServiceDetailDto("PARTY_HALL", "Party Hall", "Book the community hall for events", "AMENITY", hallSlots));


        // 5. Plumbing
        List<TimeSlot> plumbingSlots = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
             plumbingSlots.add(new TimeSlot("PLUMB-SLOT-" + i, 
                    now.plusDays(i).withHour(10).withMinute(0), 
                    now.plusDays(i).withHour(12).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("PLUMBING", "Plumbing Repair", "Schedule plumbing maintenance", "REPAIR", plumbingSlots));

        // 6. Electrical
        List<TimeSlot> electricalSlots = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
             electricalSlots.add(new TimeSlot("ELEC-SLOT-" + i, 
                    now.plusDays(i).withHour(14).withMinute(0), 
                    now.plusDays(i).withHour(16).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("ELECTRICAL", "Electrical Repair", "Schedule electrical maintenance", "REPAIR", electricalSlots));
        
        // 7. Cleaning
        List<TimeSlot> cleaningSlots = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
             cleaningSlots.add(new TimeSlot("CLEAN-SLOT-" + i, 
                    now.plusDays(i).withHour(9).withMinute(0), 
                    now.plusDays(i).withHour(11).withMinute(0), true));
        }
        services.add(new ServiceDetailDto("CLEANING", "Home Cleaning", "Full home deep cleaning", "REPAIR", cleaningSlots));
        
        // 8. Pest Control
        List<TimeSlot> pestSlots = Arrays.asList(
             new TimeSlot("PEST-SLOT-1", now.plusDays(3).withHour(11).withMinute(0), now.plusDays(3).withHour(13).withMinute(0), true)
        );
        services.add(new ServiceDetailDto("PEST_CONTROL", "Pest Control", "Pest control service", "REPAIR", pestSlots));

        return services;
    }
}
