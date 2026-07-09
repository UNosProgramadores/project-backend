package com.parking.backend.dto;

import jakarta.validation.constraints.AssertTrue;

public class ClaimVehicleRequest {

    private String plate;
    private String bikeRegistration;

    @AssertTrue(message = "Debe proporcionar placa o registro de bicicleta")
    private boolean isPlateOrBikeRegistrationProvided() {
        return (plate != null && !plate.isBlank()) || (bikeRegistration != null && !bikeRegistration.isBlank());
    }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getBikeRegistration() { return bikeRegistration; }
    public void setBikeRegistration(String bikeRegistration) { this.bikeRegistration = bikeRegistration; }
}
