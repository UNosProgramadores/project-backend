package com.parking.backend.service;

import com.parking.backend.entity.Cell;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CellServiceTest {

    @Mock
    private CellRepository cellRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @InjectMocks
    private CellService cellService;

    private Cell buildCell(Long id, String cellType) {
        Cell cell = new Cell();
        cell.setId(id);
        cell.setRow(1);
        cell.setCol(1);
        cell.setCode("1-1");
        cell.setCellType(cellType);
        cell.setStatus("available");
        return cell;
    }

    @Test
    @DisplayName("updateCellType sets cellType to parking")
    void setParkingType() {
        Cell cell = buildCell(1L, "transit");
        when(cellRepository.findById(1L)).thenReturn(Optional.of(cell));
        when(cellRepository.save(any(Cell.class))).thenAnswer(inv -> inv.getArgument(0));

        Cell result = cellService.updateCellType(1L, "parking");

        assertEquals("parking", result.getCellType());
        verify(cellRepository, times(1)).save(cell);
    }

    @Test
    @DisplayName("updateCellType to transit clears vehicleType")
    void setTransitClearsVehicleType() {
        Cell cell = buildCell(1L, "parking");
        cell.setVehicleType(new VehicleType());
        when(cellRepository.findById(1L)).thenReturn(Optional.of(cell));
        when(cellRepository.save(any(Cell.class))).thenAnswer(inv -> inv.getArgument(0));

        Cell result = cellService.updateCellType(1L, "transit");

        assertEquals("transit", result.getCellType());
        assertNull(result.getVehicleType());
    }

    @Test
    @DisplayName("updateCellType throws for invalid type")
    void invalidCellTypeThrows() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cellService.updateCellType(1L, "invalid"));

        assertTrue(ex.getMessage().contains("parking"));
    }

    @Test
    @DisplayName("updateVehicleType sets vehicleType on parking cell")
    void setVehicleTypeOnParkingCell() {
        Cell cell = buildCell(1L, "parking");
        VehicleType vt = new VehicleType();
        vt.setId(1L);
        vt.setName("car");

        when(cellRepository.findById(1L)).thenReturn(Optional.of(cell));
        when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(vt));
        when(cellRepository.save(any(Cell.class))).thenAnswer(inv -> inv.getArgument(0));

        Cell result = cellService.updateVehicleType(1L, 1L);

        assertNotNull(result.getVehicleType());
        assertEquals(1L, result.getVehicleType().getId());
    }

    @Test
    @DisplayName("updateVehicleType throws on transit cell")
    void setVehicleTypeOnTransitCellThrows() {
        Cell cell = buildCell(1L, "transit");
        when(cellRepository.findById(1L)).thenReturn(Optional.of(cell));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cellService.updateVehicleType(1L, 1L));

        assertTrue(ex.getMessage().contains("parking"));
    }

    @Test
    @DisplayName("updateVehicleType throws when vehicleType not found")
    void setVehicleTypeNotFoundThrows() {
        Cell cell = buildCell(1L, "parking");
        when(cellRepository.findById(1L)).thenReturn(Optional.of(cell));
        when(vehicleTypeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cellService.updateVehicleType(1L, 99L));

        assertTrue(ex.getMessage().contains("99"));
    }
}
