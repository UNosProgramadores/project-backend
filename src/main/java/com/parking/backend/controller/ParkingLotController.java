package com.parking.backend.controller;

import com.parking.backend.dto.ActiveEntryResponse;
import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.dto.ParkingMapResponse;
import com.parking.backend.dto.VehicleEntryRequest;
import com.parking.backend.dto.VehicleExitRequest;
import com.parking.backend.dto.VehicleExitResponse;
import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.service.CellService;
import com.parking.backend.service.EntryRecordService;
import com.parking.backend.service.ParkingLotService;
import com.parking.backend.dto.InvoiceResponse;
import com.parking.backend.repository.InvoiceRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final EntryRecordService entryRecordService;
    private final CellService cellService;
    private final InvoiceRepository invoiceRepository;

    public ParkingLotController(ParkingLotService parkingLotService,
                                EntryRecordService entryRecordService,
                                CellService cellService,
                                InvoiceRepository invoiceRepository) {
        this.parkingLotService = parkingLotService;
        this.entryRecordService = entryRecordService;
        this.cellService = cellService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAll() {
        return ResponseEntity.ok(parkingLotService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingLotService.getById(id));
    }

    @GetMapping("/{parkingLotId}/map")
    public ResponseEntity<?> getMap(@PathVariable Long parkingLotId) {
        return ResponseEntity.ok(cellService.getMap(parkingLotId));
    }

    @PostMapping
    public ResponseEntity<ParkingLot> create(@Valid @RequestBody ParkingLotRequest request) {
        return new ResponseEntity<>(parkingLotService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> update(@PathVariable Long id, @Valid @RequestBody ParkingLotRequest request) {
        return ResponseEntity.ok(parkingLotService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingLotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/discounts/toggle")
    public ResponseEntity<ParkingLot> toggleDiscounts(@PathVariable Long id) {
        return ResponseEntity.ok(parkingLotService.toggleDiscountsEnabled(id));
    }

    @GetMapping("/{parkingLotId}/active-entries")
    public ResponseEntity<List<ActiveEntryResponse>> getActiveEntries(@PathVariable Long parkingLotId) {
        return ResponseEntity.ok(entryRecordService.getActiveEntries(parkingLotId));
    }

    @GetMapping("/{parkingLotId}/invoices")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @PathVariable Long parkingLotId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        return ResponseEntity.ok(
                invoiceRepository.findByParkingLotIdWithFilters(parkingLotId, start, end, paymentMethod, minTotal, maxTotal)
                        .stream()
                        .map(InvoiceResponse::fromEntity)
                        .toList());
    }

    @PostMapping("/{parkingLotId}/entry")
    public ResponseEntity<?> registerEntry(
            @PathVariable Long parkingLotId,
            @Valid @RequestBody VehicleEntryRequest request) {
        request.setParkingLotId(parkingLotId);
        EntryRecord record = entryRecordService.registerEntry(request);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @PostMapping("/{parkingLotId}/exit")
    public ResponseEntity<?> registerExit(
            @PathVariable Long parkingLotId,
            @Valid @RequestBody VehicleExitRequest request) {
        request.setParkingLotId(parkingLotId);
        VehicleExitResponse response = entryRecordService.registerExit(request);
        return ResponseEntity.ok(response);
    }
}