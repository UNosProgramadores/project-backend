package com.parking.backend.dto;

import jakarta.validation.constraints.NotNull;

public class CellVehicleTypeRequest {

    @NotNull(message = "El tipo de vehículo es requerido")
    private Long vehicleTypeId;

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }
    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }
}
