package com.parking.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleExitRequest {

    @NotNull(message = "El id del parqueadero es requerido")
    private Long parkingLotId;

    private String plate;
    private String bikeRegistration;

    @NotBlank(message = "El método de pago es requerido")
    private String paymentMethod;

    public Long getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Long parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBikeRegistration() {
        return bikeRegistration;
    }

    public void setBikeRegistration(String bikeRegistration) {
        this.bikeRegistration = bikeRegistration;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}