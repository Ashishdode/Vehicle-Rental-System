package com.vehiclerental.service;

import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.VehicleRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    // Get booking history for a user
    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    // Book a vehicle with real-time availability check
    public Booking bookVehicle(User user, Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));

        if (!isVehicleAvailable(vehicle, startDate, endDate)) {
            throw new IllegalStateException("Vehicle is not available for the selected dates");
        }

        // Create and save booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(Booking.Status.CONFIRMED);
        vehicle.setAvailabilityStatus(false);
        vehicleRepository.save(vehicle);
        return bookingRepository.save(booking);
    }

    // Check vehicle availability for a given time range
    private boolean isVehicleAvailable(Vehicle vehicle, LocalDateTime startDate, LocalDateTime endDate) {
        List<Booking.Status> activeStatuses = Arrays.asList(Booking.Status.PENDING, Booking.Status.CONFIRMED);
        List<Booking> overlappingBookings = bookingRepository
                .findByVehicleAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        vehicle, activeStatuses, endDate, startDate);
        return overlappingBookings.isEmpty();
    }

    // Get bookings for a specific vehicle
    public List<Booking> getVehicleBookings(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        return bookingRepository.findByVehicle(vehicle);
    }

    // Get all bookings (admin only)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    @Transactional
    // Cancel a booking (user-initiated)
    public void cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if the booking belongs to the user
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("You can only cancel your own bookings");
        }

        // Check if the booking can be canceled
        LocalDateTime now = LocalDateTime.now();
        if (booking.getStatus() != Booking.Status.PENDING && booking.getStatus() != Booking.Status.CONFIRMED) {
            throw new IllegalStateException("Only PENDING or CONFIRMED bookings can be canceled");
        }

        if (now.isAfter(booking.getStartDate())) {
            throw new IllegalStateException("Cannot cancel a booking after its start date has passed");
        }

        // Cancel the booking
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        // Update vehicle availability
        Vehicle vehicle = booking.getVehicle();
        if (isVehicleAvailable(vehicle, booking.getStartDate(), booking.getEndDate())) {
            vehicle.setAvailabilityStatus(true);
            vehicleRepository.save(vehicle);
        }
    }
}