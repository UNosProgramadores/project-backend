package com.parking.backend.dto;

import java.math.BigDecimal;

public class RateRequest {

    private Long vehicleTypeId;
    private String rateType;
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
