package com.vehiclerental.controller;

import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.service.UserService;
import com.vehiclerental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    // Browse all vehicles (public)
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    // Browse available vehicles (public)
    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(vehicles);
    }

    // // Browse vehicles by type (public)
    // @GetMapping("/type/{type}")
    // public ResponseEntity<List<Vehicle>> getVehiclesByType(@PathVariable String type) {
    //     List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);
    //     return ResponseEntity.ok(vehicles);
    // }

    // Add a vehicle (admin only)
    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle,
                                              @RequestHeader("X-User-Id") Long userId) {
        User admin = userService.findById(userId);
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }
        Vehicle savedVehicle = vehicleService.addVehicle(vehicle, admin);
        return ResponseEntity.ok(savedVehicle);
    }

    // Update a vehicle (admin only)
    @PutMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long vehicleId,
                                                 @RequestBody Vehicle updatedVehicle,
                                                 @RequestHeader("X-User-Id") Long userId) {
        User admin = userService.findById(userId);
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }
        Vehicle vehicle = vehicleService.updateVehicle(vehicleId, updatedVehicle);
        return ResponseEntity.ok(vehicle);
    }

    // Remove a vehicle (admin only)
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> removeVehicle(@PathVariable Long vehicleId,
                                              @RequestHeader("X-User-Id") Long userId) {
        User admin = userService.findById(userId);
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }
        vehicleService.removeVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}