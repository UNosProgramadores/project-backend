package com.parking.backend.controller;

import com.parking.backend.dto.StaffRequest;
import com.parking.backend.entity.User;
import com.parking.backend.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<?> getAllStaff(@RequestParam Long parkingLotId) {
        List<User> staff = staffService.getAllStaff(parkingLotId);
        return ResponseEntity.ok(staff);
    }

    @PostMapping
    public ResponseEntity<?> registerStaff(@Valid @RequestBody StaffRequest request) {
        User user = staffService.registerStaff(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable Long userId) {
        User user = staffService.unlockUser(userId);
        return ResponseEntity.ok(user);
    }
}
