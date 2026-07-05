package com.parking.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class RateRequest {

    @NotNull(message = "El tipo de vehículo es requerido")
    private Long vehicleTypeId;

    @NotBlank(message = "El tipo de tarifa es requerido")
    private String rateType;

    @NotNull(message = "El costo es requerido")
    @Positive(message = "El costo debe ser positivo")
    private BigDecimal cost;
    private Boolean active;

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }
    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getRateType() {
        return rateType;
    }
    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getCost() {
        return cost;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
}
