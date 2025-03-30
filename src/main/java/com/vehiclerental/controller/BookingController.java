package com.vehiclerental.controller;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    // Get bookings for a specific vehicle (public)
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Booking>> getVehicleBookings(@PathVariable Long vehicleId) {
        List<Booking> bookings = bookingService.getVehicleBookings(vehicleId);
        return ResponseEntity.ok(bookings);
    }

    // Get booking history (authenticated user)
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getUserBookings(@RequestHeader("X-User-Id") Long userId) {
        User user = userService.findById(userId);
        List<Booking> bookings = bookingService.getUserBookings(user);
        return ResponseEntity.ok(bookings);
    }

    // Book a vehicle (authenticated user)
    @PostMapping
    public ResponseEntity<Booking> bookVehicle(@RequestHeader("X-User-Id") Long userId,
                                               @RequestParam Long vehicleId,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) {
        User user = userService.findById(userId);
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Booking booking = bookingService.bookVehicle(user, vehicleId, start, end);
        return ResponseEntity.ok(booking);
    }

    // Get all bookings (admin only)
    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings(@RequestHeader("X-User-Id") Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        if (user.getRole() == null || user.getRole() != User.Role.ADMIN) {
            throw new IllegalStateException("Only admins can view all bookings");
        }
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Cancel a booking (authenticated user)
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@RequestHeader("X-User-Id") Long userId,
                                              @PathVariable Long bookingId) {
        User user = userService.findById(userId);
        bookingService.cancelBooking(bookingId, user);
        return ResponseEntity.noContent().build();
    }
}