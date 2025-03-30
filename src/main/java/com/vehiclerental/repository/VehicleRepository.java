package com.vehiclerental.repository;

import com.vehiclerental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Find all available vehicles
    List<Vehicle> findByAvailabilityStatusTrue();
    List<Vehicle> findByType(String type);

}