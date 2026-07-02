package com.parking.backend.controller;

import com.parking.backend.dto.RateRequest;
import com.parking.backend.entity.Rate;
import com.parking.backend.service.RateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots/{parkingLotId}/rates")
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping
    public ResponseEntity<List<Rate>> getRates(@PathVariable Long parkingLotId) {
        return ResponseEntity.ok(rateService.getByParkingLot(parkingLotId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rate> getRate(@PathVariable Long parkingLotId, @PathVariable Long id) {
        return ResponseEntity.ok(rateService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Rate> createRate(
            @PathVariable Long parkingLotId,
            @RequestBody RateRequest request) {
        return new ResponseEntity<>(rateService.create(parkingLotId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rate> updateRate(
            @PathVariable Long parkingLotId,
            @PathVariable Long id,
            @RequestBody RateRequest request) {
        return ResponseEntity.ok(rateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long parkingLotId, @PathVariable Long id) {
        rateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
