package com.parking.backend.dto;

import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.Invoice;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.Vehicle;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private LocalDateTime issuedAt;
    private Long paymentId;
    private String vehicleType;
    private String plate;
    private String bikeRegistration;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Integer duration;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalPaid;
    private String paymentMethod;
    private String cellCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getBikeRegistration() { return bikeRegistration; }
    public void setBikeRegistration(String bikeRegistration) { this.bikeRegistration = bikeRegistration; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }

    public static InvoiceResponse fromEntity(Invoice invoice) {
        Payment payment = invoice.getPayment();
        EntryRecord record = payment.getEntryRecord();
        Vehicle vehicle = record.getVehicle();
        InvoiceResponse res = new InvoiceResponse();
        res.setId(invoice.getId());
        res.setInvoiceNumber(invoice.getInvoiceNumber());
        res.setIssuedAt(invoice.getIssuedAt());
        res.setPaymentId(payment.getId());
        res.setVehicleType(vehicle.getVehicleType().getName());
        res.setPlate(vehicle.getPlate());
        res.setBikeRegistration(vehicle.getBikeRegistration());
        res.setEntryTime(record.getEntryTime());
        res.setExitTime(record.getExitTime());
        res.setDuration(record.getDuration());
        res.setSubtotal(payment.getSubtotal());
        res.setDiscountAmount(payment.getDiscountAmount());
        res.setTotalPaid(payment.getTotalPaid());
        res.setPaymentMethod(payment.getPaymentMethod());
        res.setCellCode(record.getCell().getCode());
        return res;
    }
}
