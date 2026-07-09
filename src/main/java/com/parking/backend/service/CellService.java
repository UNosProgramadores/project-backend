package com.parking.backend.service;

import com.parking.backend.dto.CellDto;
import com.parking.backend.dto.ParkingMapResponse;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public ParkingMapResponse getMap(Long parkingLotId) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        List<Cell> cells = cellRepository.findByParkingLot(lot);

        List<List<CellDto>> grid = new ArrayList<>();
        for (int r = 0; r < lot.getRows(); r++) {
            List<CellDto> row = new ArrayList<>();
            for (int c = 0; c < lot.getColumns(); c++) {
                row.add(null);
            }
            grid.add(row);
        }

        for (Cell cell : cells) {
            if (Boolean.FALSE.equals(cell.getActive()) || cell.getRow() < 0 || cell.getCol() < 0) continue;
            CellDto dto = new CellDto(
                    cell.getId(),
                    cell.getRow(),
                    cell.getCol(),
                    cell.getCode(),
                    cell.getCellType(),
                    cell.getStatus(),
                    cell.getVehicleType() != null ? cell.getVehicleType().getId() : null,
                    cell.getVehicleType() != null ? cell.getVehicleType().getName() : null,
                    cell.getReservedForStaff()
            );
            grid.get(cell.getRow()).set(cell.getCol(), dto);
        }

        return new ParkingMapResponse(
                lot.getId(), lot.getName(),
                lot.getRows(), lot.getColumns(), grid
        );
    }

    public Cell getById(Long id) {
        return cellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Celda no encontrada con ID: " + id));
    }

    @Transactional
    public Cell updateCellType(Long cellId, String cellType, Long parkingLotId) {
        if (!"parking".equals(cellType) && !"transit".equals(cellType)) {
            throw new RuntimeException("El tipo de celda debe ser 'parking' o 'transit'");
        }
        Cell cell = getById(cellId);
        if (!cell.getParkingLot().getId().equals(parkingLotId)) {
            throw new RuntimeException("La celda no pertenece a este parqueadero");
        }
        cell.setCellType(cellType);
        if ("transit".equals(cellType)) {
            cell.setVehicleType(null);
        }
        return cellRepository.save(cell);
    }

    @Transactional
    public Cell updateVehicleType(Long cellId, Long vehicleTypeId, Long parkingLotId) {
        Cell cell = getById(cellId);
        if (!cell.getParkingLot().getId().equals(parkingLotId)) {
            throw new RuntimeException("La celda no pertenece a este parqueadero");
        }
        if (!"parking".equals(cell.getCellType())) {
            throw new RuntimeException("Solo se puede asignar tipo de vehículo a celdas de tipo 'parking'");
        }
        VehicleType vt = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con ID: " + vehicleTypeId));
        cell.setVehicleType(vt);
        return cellRepository.save(cell);
    }
}
