package com.parking.backend.service;

import com.parking.backend.dto.CellDto;
import com.parking.backend.dto.ParkingMapResponse;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CellServiceTest {

    @Mock
    private CellRepository cellRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @Mock
    private ParkingLotService parkingLotService;

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

    private ParkingLot buildLot(int rows, int cols) {
        ParkingLot lot = new ParkingLot();
        lot.setId(1L);
        lot.setName("Test Lot");
        lot.setRows(rows);
        lot.setColumns(cols);
        return lot;
    }

    private Cell buildMapCell(Long id, int row, int col, String code, String cellType,
                              String status, VehicleType vt, boolean staff) {
        Cell cell = new Cell();
        cell.setId(id);
        cell.setRow(row);
        cell.setCol(col);
        cell.setCode(code);
        cell.setCellType(cellType);
        cell.setStatus(status);
        cell.setVehicleType(vt);
        cell.setReservedForStaff(staff);
        return cell;
    }

    @Test
    @DisplayName("getMap returns grid with correct dimensions")
    void getMapReturnsCorrectDimensions() {
        ParkingLot lot = buildLot(3, 4);
        List<Cell> cells = new ArrayList<>();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 4; c++) {
                cells.add(buildMapCell((long) ((r - 1) * 4 + c), r, c, r + "-" + c,
                        "parking", "available", null, false));
            }
        }
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(cellRepository.findByParkingLot(lot)).thenReturn(cells);

        ParkingMapResponse response = cellService.getMap(1L);

        assertEquals(1L, response.getParkingLotId());
        assertEquals("Test Lot", response.getParkingLotName());
        assertEquals(3, response.getRows());
        assertEquals(4, response.getColumns());
        assertEquals(3, response.getGrid().size());
        assertEquals(4, response.getGrid().get(0).size());
        assertNotNull(response.getGrid().get(0).get(0));
        assertEquals("1-1", response.getGrid().get(0).get(0).getCode());
        assertEquals("2-3", response.getGrid().get(1).get(2).getCode());
    }

    @Test
    @DisplayName("getMap returns grid with mixed cells (parking, transit, different vehicle types, occupied, staff)")
    void getMapWithMixedCells() {
        ParkingLot lot = buildLot(2, 3);
        VehicleType car = new VehicleType();
        car.setId(1L);
        car.setName("car");
        VehicleType motorcycle = new VehicleType();
        motorcycle.setId(2L);
        motorcycle.setName("motorcycle");

        List<Cell> cells = List.of(
                buildMapCell(1L, 1, 1, "1-1", "parking", "occupied", car, false),
                buildMapCell(2L, 1, 2, "1-2", "transit", "available", null, false),
                buildMapCell(3L, 1, 3, "1-3", "parking", "available", motorcycle, true),
                buildMapCell(4L, 2, 1, "2-1", "parking", "available", null, false),
                buildMapCell(5L, 2, 2, "2-2", "parking", "occupied", car, false),
                buildMapCell(6L, 2, 3, "2-3", "parking", "available", null, false)
        );
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(cellRepository.findByParkingLot(lot)).thenReturn(cells);

        ParkingMapResponse response = cellService.getMap(1L);

        assertEquals(2, response.getRows());
        assertEquals(3, response.getColumns());

        CellDto c11 = response.getGrid().get(0).get(0);
        assertEquals("occupied", c11.getStatus());
        assertEquals("parking", c11.getCellType());
        assertEquals(1L, c11.getVehicleTypeId());
        assertEquals("car", c11.getVehicleTypeName());
        assertFalse(c11.getReservedForStaff());

        CellDto c12 = response.getGrid().get(0).get(1);
        assertEquals("transit", c12.getCellType());
        assertEquals("available", c12.getStatus());
        assertNull(c12.getVehicleTypeId());
        assertNull(c12.getVehicleTypeName());

        CellDto c13 = response.getGrid().get(0).get(2);
        assertEquals("parking", c13.getCellType());
        assertEquals("available", c13.getStatus());
        assertEquals(2L, c13.getVehicleTypeId());
        assertEquals("motorcycle", c13.getVehicleTypeName());
        assertTrue(c13.getReservedForStaff());

        CellDto c22 = response.getGrid().get(1).get(1);
        assertEquals("occupied", c22.getStatus());
        assertEquals(1L, c22.getVehicleTypeId());
        assertEquals("car", c22.getVehicleTypeName());
        assertFalse(c22.getReservedForStaff());

        assertNull(response.getGrid().get(1).get(2).getVehicleTypeId());
    }

    @Test
    @DisplayName("getMap with 1x1 parking lot (minimum case)")
    void getMapWithOneByOne() {
        ParkingLot lot = buildLot(1, 1);
        Cell cell = buildMapCell(1L, 1, 1, "1-1", "parking", "available", null, false);
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(cellRepository.findByParkingLot(lot)).thenReturn(List.of(cell));

        ParkingMapResponse response = cellService.getMap(1L);

        assertEquals(1, response.getRows());
        assertEquals(1, response.getColumns());
        assertEquals(1, response.getGrid().size());
        assertEquals(1, response.getGrid().get(0).size());
        assertNotNull(response.getGrid().get(0).get(0));
        assertEquals("1-1", response.getGrid().get(0).get(0).getCode());
    }

    @Test
    @DisplayName("getMap handles missing cells gracefully (null positions in grid)")
    void getMapWithMissingCells() {
        ParkingLot lot = buildLot(2, 3);
        List<Cell> cells = List.of(
                buildMapCell(1L, 1, 1, "1-1", "parking", "available", null, false),
                buildMapCell(3L, 1, 3, "1-3", "parking", "occupied", null, false),
                buildMapCell(4L, 2, 1, "2-1", "parking", "available", null, false)
        );
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(cellRepository.findByParkingLot(lot)).thenReturn(cells);

        ParkingMapResponse response = cellService.getMap(1L);

        assertNotNull(response.getGrid().get(0).get(0));
        assertNull(response.getGrid().get(0).get(1));
        assertNotNull(response.getGrid().get(0).get(2));
        assertNotNull(response.getGrid().get(1).get(0));
        assertNull(response.getGrid().get(1).get(1));
        assertNull(response.getGrid().get(1).get(2));
    }

    @Test
    @DisplayName("getMap throws when parking lot not found")
    void getMapThrowsWhenLotNotFound() {
        when(parkingLotService.getById(99L)).thenThrow(new RuntimeException("Parqueadero no encontrado con ID: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cellService.getMap(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(cellRepository, never()).findByParkingLot(any());
    }
}
