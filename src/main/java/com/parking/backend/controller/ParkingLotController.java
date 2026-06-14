package com.parking.backend.controller;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.entity.ParkingLot;
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

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
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
}
