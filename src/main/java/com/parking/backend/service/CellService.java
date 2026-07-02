package com.parking.backend.service;

import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CellService {

    private final CellRepository cellRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final ParkingLotService parkingLotService;

    public CellService(CellRepository cellRepository,
                       VehicleTypeRepository vehicleTypeRepository,
                       ParkingLotService parkingLotService) {
        this.cellRepository = cellRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.parkingLotService = parkingLotService;
    }

    public List<Cell> getByParkingLot(Long parkingLotId) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        return cellRepository.findByParkingLot(lot);
    }

    public Cell getById(Long id) {
        return cellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Celda no encontrada con ID: " + id));
    }

    @Transactional
    public Cell updateCellType(Long cellId, String cellType) {
        if (!"parking".equals(cellType) && !"transit".equals(cellType)) {
            throw new RuntimeException("El tipo de celda debe ser 'parking' o 'transit'");
        }
        Cell cell = getById(cellId);
        cell.setCellType(cellType);
        if ("transit".equals(cellType)) {
            cell.setVehicleType(null);
        }
        return cellRepository.save(cell);
    }

    @Transactional
    public Cell updateVehicleType(Long cellId, Long vehicleTypeId) {
        Cell cell = getById(cellId);
        if (!"parking".equals(cell.getCellType())) {
            throw new RuntimeException("Solo se puede asignar tipo de vehículo a celdas de tipo 'parking'");
        }
        VehicleType vt = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con ID: " + vehicleTypeId));
        cell.setVehicleType(vt);
        return cellRepository.save(cell);
    }
}
