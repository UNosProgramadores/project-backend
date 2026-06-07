package com.parking.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(unique = true)
    private String plate;

    // Identifier for bicycles, replaces plate
    @Column(name = "bike_registration", unique = true)
    private String bikeRegistration;

    // Nullable: a bicycle can enter without a registered owner (RF_10)
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String brand;
    private String model;
    private String color;
    private Boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getBikeRegistration() { return bikeRegistration; }
    public void setBikeRegistration(String bikeRegistration) { this.bikeRegistration = bikeRegistration; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}