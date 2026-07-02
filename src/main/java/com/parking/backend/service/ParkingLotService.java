package com.parking.backend.service;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.ParkingLotRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final CellRepository cellRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository,
                             CellRepository cellRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.cellRepository = cellRepository;
    }

    public List<ParkingLot> getAll() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot getById(Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parqueadero no encontrado con ID: " + id));
    }

    @Transactional
    public ParkingLot create(ParkingLotRequest request) {
        validateHours(request);

        ParkingLot parkingLot = new ParkingLot();
        updateEntityFromRequest(parkingLot, request);

        parkingLot = parkingLotRepository.save(parkingLot);
        generateCells(parkingLot, request.getRows(), request.getColumns());
        return parkingLot;
    }

    @Transactional
    public ParkingLot update(Long id, ParkingLotRequest request) {
        validateHours(request);

        ParkingLot parkingLot = getById(id);
        int oldRows = parkingLot.getRows();
        int oldCols = parkingLot.getColumns();
        updateEntityFromRequest(parkingLot, request);

        parkingLot = parkingLotRepository.save(parkingLot);
        syncCells(parkingLot, request.getRows(), request.getColumns(), oldRows, oldCols);
        return parkingLot;
    }

    @Transactional
    public ParkingLot toggleDiscountsEnabled(Long id) {
        ParkingLot lot = getById(id);
        lot.setDiscountsEnabled(!Boolean.TRUE.equals(lot.getDiscountsEnabled()));
        return parkingLotRepository.save(lot);
    }

    @Transactional
    public void delete(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Parqueadero no encontrado con ID: " + id);
        }
        parkingLotRepository.deleteById(id);
    }

    private void validateHours(ParkingLotRequest request) {
        if (request.getOpeningTime().isAfter(request.getClosingTime()) ||
            request.getOpeningTime().equals(request.getClosingTime())) {
            throw new RuntimeException("La hora de apertura debe ser anterior a la hora de cierre");
        }
    }

    private void updateEntityFromRequest(ParkingLot entity, ParkingLotRequest request) {
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setOpeningTime(request.getOpeningTime());
        entity.setClosingTime(request.getClosingTime());
        entity.setRows(request.getRows());
        entity.setColumns(request.getColumns());
        entity.setAutoAssignment(request.getAutoAssignment());
        entity.setDiscountsEnabled(request.getDiscountsEnabled());
    }

    private void generateCells(ParkingLot lot, int rows, int columns) {
        List<Cell> cells = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= columns; c++) {
                cells.add(createCell(lot, r, c));
            }
        }
        cellRepository.saveAll(cells);
    }

    // ponytail: no reallocation logic — add new cells on growth, reject shrink if occupied
    private void syncCells(ParkingLot lot, int newRows, int newCols, int oldRows, int oldCols) {
        List<Cell> existing = cellRepository.findByParkingLot(lot);

        if (newRows < oldRows || newCols < oldCols) {
            List<Cell> toRemove = existing.stream()
                    .filter(c -> c.getRow() > newRows || c.getCol() > newCols)
                    .collect(Collectors.toList());
            for (Cell c : toRemove) {
                if ("occupied".equals(c.getStatus())) {
                    throw new RuntimeException("No se puede reducir: la celda " + c.getCode() + " está ocupada");
                }
            }
            try {
                cellRepository.deleteAll(toRemove);
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("No se puede reducir: algunas celdas tienen registros de entrada asociados");
            }
        }

        if (newRows > oldRows || newCols > oldCols) {
            Set<String> existingPos = existing.stream()
                    .map(c -> c.getRow() + "," + c.getCol())
                    .collect(Collectors.toSet());
            List<Cell> newCells = new ArrayList<>();
            for (int r = 1; r <= newRows; r++) {
                for (int c = 1; c <= newCols; c++) {
                    if (!existingPos.contains(r + "," + c)) {
                        newCells.add(createCell(lot, r, c));
                    }
                }
            }
            cellRepository.saveAll(newCells);
        }
    }

    private Cell createCell(ParkingLot lot, int row, int col) {
        Cell cell = new Cell();
        cell.setParkingLot(lot);
        cell.setRow(row);
        cell.setCol(col);
        cell.setCode(row + "-" + col);
        cell.setCellType("parking");
        cell.setStatus("available");
        cell.setReservedForStaff(false);
        return cell;
    }
}
