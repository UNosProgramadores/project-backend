package com.parking.backend.controller;

import com.parking.backend.dto.CellTypeRequest;
import com.parking.backend.dto.CellVehicleTypeRequest;
import com.parking.backend.entity.Cell;
import com.parking.backend.service.CellService;
import com.parking.backend.service.ParkingLotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots/{parkingLotId}/cells")
public class CellController {

    private final CellService cellService;
    private final ParkingLotService parkingLotService;

    public CellController(CellService cellService, ParkingLotService parkingLotService) {
        this.cellService = cellService;
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<Cell>> getCells(@PathVariable Long parkingLotId) {
        return ResponseEntity.ok(cellService.getByParkingLot(parkingLotId));
    }

    @PatchMapping("/{cellId}/type")
    public ResponseEntity<?> updateCellType(
            @PathVariable Long parkingLotId,
            @PathVariable Long cellId,
            @RequestBody CellTypeRequest request) {
        try {
            Cell cell = cellService.updateCellType(cellId, request.getCellType());
            return ResponseEntity.ok(cell);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{cellId}/vehicle-type")
    public ResponseEntity<?> updateVehicleType(
            @PathVariable Long parkingLotId,
            @PathVariable Long cellId,
            @RequestBody CellVehicleTypeRequest request) {
        try {
            Cell cell = cellService.updateVehicleType(cellId, request.getVehicleTypeId());
            return ResponseEntity.ok(cell);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
