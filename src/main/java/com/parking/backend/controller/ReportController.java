package com.parking.backend.controller;

import com.parking.backend.dto.ReportResponse;
import com.parking.backend.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/parking-lots/{parkingLotId}/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<?> getReport(
            @PathVariable Long parkingLotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ReportResponse response = reportService.generateReport(parkingLotId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
