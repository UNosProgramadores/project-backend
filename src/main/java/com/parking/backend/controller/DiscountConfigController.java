package com.parking.backend.controller;

import com.parking.backend.dto.DiscountConfigRequest;
import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.service.DiscountConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots/{parkingLotId}/discounts")
public class DiscountConfigController {

    private final DiscountConfigService discountConfigService;

    public DiscountConfigController(DiscountConfigService discountConfigService) {
        this.discountConfigService = discountConfigService;
    }

    @GetMapping("/config")
    public ResponseEntity<List<DiscountConfig>> getConfigs(@PathVariable Long parkingLotId) {
        return ResponseEntity.ok(discountConfigService.getByParkingLot(parkingLotId));
    }

    @GetMapping("/config/{id}")
    public ResponseEntity<DiscountConfig> getConfig(@PathVariable Long parkingLotId, @PathVariable Long id) {
        return ResponseEntity.ok(discountConfigService.getById(id));
    }

    @PostMapping("/config")
    public ResponseEntity<DiscountConfig> createConfig(
            @PathVariable Long parkingLotId,
            @RequestBody DiscountConfigRequest request) {
        return new ResponseEntity<>(discountConfigService.create(parkingLotId, request), HttpStatus.CREATED);
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<DiscountConfig> updateConfig(
            @PathVariable Long parkingLotId,
            @PathVariable Long id,
            @RequestBody DiscountConfigRequest request) {
        return ResponseEntity.ok(discountConfigService.update(id, request));
    }

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long parkingLotId, @PathVariable Long id) {
        discountConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
