package com.parking.backend.dto;

import com.parking.backend.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceResponseTest {

    @Test
    @DisplayName("fromEntity maps all fields including cellCode")
    void fromEntityMapsAllFields() {
        ParkingLot lot = new ParkingLot();
        lot.setId(1L);

        Cell cell = new Cell();
        cell.setId(10L);
        cell.setParkingLot(lot);
        cell.setCode("C-10");

        VehicleType vt = new VehicleType();
        vt.setId(1L);
        vt.setName("car");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(100L);
        vehicle.setPlate("ABC-123");
        vehicle.setVehicleType(vt);

        EntryRecord record = new EntryRecord();
        record.setId(1000L);
        record.setVehicle(vehicle);
        record.setCell(cell);
        record.setEntryTime(LocalDateTime.of(2025, 1, 15, 10, 30));
        record.setExitTime(LocalDateTime.of(2025, 1, 15, 12, 30));
        record.setDuration(120);

        Payment payment = new Payment();
        payment.setId(500L);
        payment.setEntryRecord(record);
        payment.setSubtotal(new BigDecimal("100.00"));
        payment.setDiscountAmount(new BigDecimal("10.00"));
        payment.setTotalPaid(new BigDecimal("90.00"));
        payment.setPaymentMethod("CASH");

        Invoice invoice = new Invoice();
        invoice.setId(999L);
        invoice.setPayment(payment);
        invoice.setInvoiceNumber("INV-001");
        invoice.setIssuedAt(LocalDateTime.of(2025, 1, 15, 12, 31));

        InvoiceResponse res = InvoiceResponse.fromEntity(invoice);

        assertEquals(999L, res.getId());
        assertEquals("INV-001", res.getInvoiceNumber());
        assertEquals(invoice.getIssuedAt(), res.getIssuedAt());
        assertEquals(500L, res.getPaymentId());
        assertEquals("car", res.getVehicleType());
        assertEquals("ABC-123", res.getPlate());
        assertNull(res.getBikeRegistration());
        assertEquals(record.getEntryTime(), res.getEntryTime());
        assertEquals(record.getExitTime(), res.getExitTime());
        assertEquals(120, res.getDuration());
        assertEquals(new BigDecimal("100.00"), res.getSubtotal());
        assertEquals(new BigDecimal("10.00"), res.getDiscountAmount());
        assertEquals(new BigDecimal("90.00"), res.getTotalPaid());
        assertEquals("CASH", res.getPaymentMethod());
        assertEquals("C-10", res.getCellCode());
    }
}
