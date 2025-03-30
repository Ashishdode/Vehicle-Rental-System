package com.vehiclerental.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; 

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "availability_status", nullable = false)
    private boolean availabilityStatus = true;

    @ManyToOne
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
