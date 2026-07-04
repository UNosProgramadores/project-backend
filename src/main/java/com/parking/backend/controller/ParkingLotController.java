package com.parking.backend.controller;

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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final EntryRecordService entryRecordService;
    private final CellService cellService;

    public ParkingLotController(ParkingLotService parkingLotService,
                                EntryRecordService entryRecordService,
                                CellService cellService) {
        this.parkingLotService = parkingLotService;
        this.entryRecordService = entryRecordService;
        this.cellService = cellService;
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
        try {
            return ResponseEntity.ok(cellService.getMap(parkingLotId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @PostMapping("/{parkingLotId}/entry")
    public ResponseEntity<?> registerEntry(
            @PathVariable Long parkingLotId,
            @RequestBody VehicleEntryRequest request) {
        try {
            request.setParkingLotId(parkingLotId);
            EntryRecord record = entryRecordService.registerEntry(request);
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{parkingLotId}/exit")
    public ResponseEntity<?> registerExit(
            @PathVariable Long parkingLotId,
            @RequestBody VehicleExitRequest request) {
        try {
            request.setParkingLotId(parkingLotId);
            VehicleExitResponse response = entryRecordService.registerExit(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}