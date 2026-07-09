package com.parking.backend.dto;

public class VehicleTypeResponse {

    private Long id;
    private String name;
    private Boolean requiresPlate;

    public VehicleTypeResponse() {}

    public VehicleTypeResponse(Long id, String name, Boolean requiresPlate) {
        this.id = id;
        this.name = name;
        this.requiresPlate = requiresPlate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getRequiresPlate() { return requiresPlate; }
    public void setRequiresPlate(Boolean requiresPlate) { this.requiresPlate = requiresPlate; }
}
