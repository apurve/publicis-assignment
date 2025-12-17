package com.example.bookingservice.service;

import com.example.bookingservice.model.Amenity;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.User;
import com.example.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Long userId, String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. Validate User (Mock)
        User user = new User(userId, "John Doe", true); // Mock user fetch
        if (!user.isMaintenanceFeePaid()) {
            throw new RuntimeException("Maintenance fee not paid for user: " + userId);
        }

        // 2. Validate Amenity (Mock)
        Amenity amenity = new Amenity(amenityId, "Gym", true); // Mock amenity fetch
        if (!amenity.isAvailable()) {
            throw new RuntimeException("Amenity not available: " + amenityId);
        }

        // 3. Create Booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setAmenityId(amenityId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("CONFIRMED");

        // 4. Save to Repository
        return bookingRepository.save(booking);
    }
}
