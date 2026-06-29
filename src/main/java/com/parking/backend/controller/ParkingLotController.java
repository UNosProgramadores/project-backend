package com.parking.backend.controller;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.dto.VehicleExitRequest;
import com.parking.backend.dto.VehicleExitResponse;
import com.parking.backend.entity.ParkingLot;
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

    public ParkingLotController(ParkingLotService parkingLotService,
                                EntryRecordService entryRecordService) {
        this.parkingLotService = parkingLotService;
        this.entryRecordService = entryRecordService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAll() {
        return ResponseEntity.ok(parkingLotService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingLotService.getById(id));
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

    @PostMapping("/{parkingLotId}/exit")
    public ResponseEntity<VehicleExitResponse> registerExit(
            @PathVariable Long parkingLotId,
            @RequestBody VehicleExitRequest request) {
        try {
            VehicleExitResponse response = entryRecordService.registerExit(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}