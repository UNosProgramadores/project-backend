package com.parking.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_config")
public class DiscountConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    private Boolean active;

    // Null if this discount is visit-based, not invoice-based (RF_06)
    @Column(name = "min_external_invoice")
    private BigDecimal minExternalInvoice;

    // Null if this discount is invoice-based, not visit-based (RF_07)
    @Column(name = "min_visits")
    private Integer minVisits;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    // null end_date = currently active config (supports RF_08 history)
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }
    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

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
