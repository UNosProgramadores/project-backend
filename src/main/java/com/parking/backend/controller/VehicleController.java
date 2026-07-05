package com.parking.backend.controller;

import com.parking.backend.dto.ClaimVehicleRequest;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.service.VehicleService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> claimVehicle(@Valid @RequestBody ClaimVehicleRequest request) {
        Vehicle vehicle = vehicleService.claimVehicle(request);
        return ResponseEntity.ok(vehicle);
    }
}
