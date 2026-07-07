package com.parking.backend.controller;

import com.parking.backend.dto.InvoiceResponse;
import com.parking.backend.entity.Invoice;
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
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        return ResponseEntity.ok(toResponse(invoice));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.fromEntity(invoice);
    }
}
