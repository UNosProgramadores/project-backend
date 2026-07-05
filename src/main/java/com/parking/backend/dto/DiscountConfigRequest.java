package com.parking.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountConfigRequest {

    private BigDecimal minExternalInvoice;
    private Integer minVisits;

    @NotNull(message = "El porcentaje de descuento es requerido")
    @Min(value = 0, message = "El porcentaje de descuento debe ser mayor o igual a 0")
    @Max(value = 100, message = "El porcentaje de descuento debe ser menor o igual a 100")
    private BigDecimal discountPercentage;
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public BigDecimal getMinExternalInvoice() {
        return minExternalInvoice;
    }
    public void setMinExternalInvoice(BigDecimal minExternalInvoice) {
        this.minExternalInvoice = minExternalInvoice;
    }

    public Integer getMinVisits() {
        return minVisits;
    }
    public void setMinVisits(Integer minVisits) {
        this.minVisits = minVisits;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
