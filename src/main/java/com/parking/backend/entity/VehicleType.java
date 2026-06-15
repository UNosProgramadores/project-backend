package com.parking.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_type")
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(name = "requires_plate")
    private Boolean requiresPlate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequiresPlate() {
        return requiresPlate;
    }
    public void setRequiresPlate(Boolean requiresPlate) {
        this.requiresPlate = requiresPlate;
    }
}
