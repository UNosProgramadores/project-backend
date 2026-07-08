package com.parking.backend.controller;

import com.parking.backend.repository.UserRepository;
import com.parking.backend.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final UserRepository userRepository;
    private final VehicleService vehicleService;

    public CustomerController(UserRepository userRepository,
                              VehicleService vehicleService) {
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByDocument(@RequestParam String document) {
        return userRepository.findByDocumentAndRole_Name(document, "customer")
                .map(u -> ResponseEntity.ok(Map.of(
                        "userId", u.getId(),
                        "name", u.getName(),
                        "document", u.getDocument()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/vehicles")
    public ResponseEntity<?> getMyVehicles() {
        return ResponseEntity.ok(vehicleService.getMyVehicles());
    }
}
