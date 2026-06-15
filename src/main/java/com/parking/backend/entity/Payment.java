package com.parking.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One entry record has at most one payment
    @OneToOne
    @JoinColumn(name = "entry_record_id", unique = true)
    private EntryRecord entryRecord;

    private BigDecimal subtotal;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "total_paid")
    private BigDecimal totalPaid;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Number or description of the external invoice that justified the discount
    // Null if no external invoice discount was applied
    @Column(name = "external_invoice_ref")
    private String externalInvoiceRef;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public EntryRecord getEntryRecord() {
        return entryRecord;
    }
    public void setEntryRecord(EntryRecord entryRecord) {
        this.entryRecord = entryRecord;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }
    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getExternalInvoiceRef() {
        return externalInvoiceRef;
    }
    public void setExternalInvoiceRef(String externalInvoiceRef) {
        this.externalInvoiceRef = externalInvoiceRef;
    }
}
