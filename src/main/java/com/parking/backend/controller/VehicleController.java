package com.parking.backend.controller;

import com.parking.backend.dto.ClaimVehicleRequest;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/claim")
    public ResponseEntity<?> claimVehicle(@RequestBody ClaimVehicleRequest request) {
        try {
            Vehicle vehicle = vehicleService.claimVehicle(request);
            return ResponseEntity.ok(vehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
