package com.parking.backend.controller;

import com.parking.backend.dto.InvoiceResponse;
import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.Invoice;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.repository.InvoiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var opt = invoiceRepository.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(toResponse(opt.get()));
        }
        return ResponseEntity.badRequest().body("Factura no encontrada con ID: " + id);
    }

    private InvoiceResponse toResponse(Invoice invoice) {
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
        return res;
    }
}
