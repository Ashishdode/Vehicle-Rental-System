package com.vehiclerental.repository;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings by vehicle
    List<Booking> findByVehicle(Vehicle vehicle);
    
    // Find bookings for a specific user (for booking history)
    List<Booking> findByUser(User user);

    // Find overlapping bookings for a vehicle to check availability
    List<Booking> findByVehicleAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Vehicle vehicle, List<Booking.Status> statuses, LocalDateTime end, LocalDateTime start);
}