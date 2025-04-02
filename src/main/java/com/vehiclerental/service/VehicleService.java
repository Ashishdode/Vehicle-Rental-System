package com.vehiclerental.service;

import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.VehicleRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        // Update the availability status of each vehicle
        allVehicles.forEach(vehicle -> {
            isVehicleAvailable(vehicle);
        });
        return allVehicles;
    }

    // Get all available vehicles (for users to browse)
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        return allVehicles.stream()
                .filter(this::isVehicleAvailable)
                .collect(Collectors.toList());
    }

    // Check if a vehicle is available based on booking dates
    private boolean isVehicleAvailable(Vehicle vehicle) {
        List<Booking> bookings = bookingRepository.findByVehicle(vehicle);
        LocalDateTime now = LocalDateTime.now();
        logger.info("Checking availability for vehicle {} at time {}", vehicle.getVehicleId(), now);
    
        if (bookings.isEmpty()) {
            vehicle.setAvailabilityStatus(true);
            vehicleRepository.save(vehicle);
            return true;
        }
    
        boolean hasActiveBooking = bookings.stream()
                .anyMatch(booking -> {
                    boolean isActive = booking.getStatus() == Booking.Status.CONFIRMED &&
                            now.isAfter(booking.getStartDate()) &&
                            now.isBefore(booking.getEndDate());
                    logger.info("Booking {} for vehicle {}: isActive={}", booking.getBookingId(), vehicle.getVehicleId(), isActive);
                    return isActive;
                });
    
        vehicle.setAvailabilityStatus(!hasActiveBooking);
        vehicleRepository.save(vehicle);
        logger.info("Vehicle {} availability status updated to {}", vehicle.getVehicleId(), vehicle.isAvailabilityStatus());
        return !hasActiveBooking;
    }
    // Get vehicles by type (e.g., "Car", "Bike")
    public List<Vehicle> getVehiclesByType(String type) {
        return vehicleRepository.findByType(type);
    }

    // Add a new vehicle (admin only)
    public Vehicle addVehicle(Vehicle vehicle, User admin) {
        vehicle.setAddedBy(admin);
        return vehicleRepository.save(vehicle);
    }

    // Update vehicle details (admin only)
    public Vehicle updateVehicle(Long vehicleId, Vehicle updatedVehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        existingVehicle.setName(updatedVehicle.getName());
        existingVehicle.setType(updatedVehicle.getType());
        existingVehicle.setDescription(updatedVehicle.getDescription());
        existingVehicle.setAvailabilityStatus(updatedVehicle.isAvailabilityStatus());
        return vehicleRepository.save(existingVehicle);
    }

    @Transactional
    // Remove a vehicle (admin only)
public void removeVehicle(Long vehicleId) {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));

    List<Booking> bookings = bookingRepository.findByVehicle(vehicle);
    LocalDateTime now = LocalDateTime.now();

    logger.info("Attempting to remove vehicle {} - Found {} bookings", vehicleId, bookings.size());

    boolean hasActiveBooking = bookings.stream().anyMatch(booking ->
            booking.getStatus() == Booking.Status.CONFIRMED &&
            (now.isAfter(booking.getStartDate()) && now.isBefore(booking.getEndDate())|| now.isBefore(booking.getStartDate())));

    if (hasActiveBooking) {
        logger.warn("Cannot remove vehicle {} due to active bookings or upcoming bookings", vehicleId);
        throw new IllegalStateException("Cannot remove vehicle with active bookings or upcoming bookings");
    }

    if (!vehicleRepository.existsById(vehicle.getVehicleId())) {
        vehicleRepository.save(vehicle);
    }

    //Remove past bookings
    List<Booking> pastBookings = bookings.stream()
            .filter(booking -> now.isAfter(booking.getEndDate()))
            .toList();
    
    if (!pastBookings.isEmpty()) {
        logger.info("Deleting {} past bookings for vehicle {}", pastBookings.size(), vehicleId);
        bookingRepository.deleteAll(pastBookings);
    }

    //Remove vehicle**
    try {
        vehicleRepository.deleteById(vehicleId);
        logger.info("Vehicle {} successfully deleted.", vehicleId);
    } catch (Exception e) {
        logger.error("Error occurred while deleting vehicle {}: {}", vehicleId, e.getMessage(), e);
        throw new IllegalStateException("An unexpected error occurred while removing the vehicle: " + e.getMessage());
    }
}

}