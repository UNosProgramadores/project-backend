package com.parking.backend.service;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private CellRepository cellRepository;

    @InjectMocks
    private ParkingLotService parkingLotService;


    private ParkingLotRequest buildRequest(LocalTime opening, LocalTime closing) {
        ParkingLotRequest req = new ParkingLotRequest();
        req.setName("ParKing Centro");
        req.setAddress("Calle 100 # 15-20, Bogotá");
        req.setOpeningTime(opening);
        req.setClosingTime(closing);
        req.setRows(5);
        req.setColumns(10);
        req.setAutoAssignment(false);
        req.setDiscountsEnabled(false);
        return req;
    }

    private ParkingLot buildSavedLot(Long id) {
        ParkingLot lot = new ParkingLot();
        lot.setId(id);
        lot.setName("ParKing Centro");
        lot.setAddress("Calle 100 # 15-20, Bogotá");
        lot.setOpeningTime(LocalTime.of(6, 0));
        lot.setClosingTime(LocalTime.of(22, 0));
        lot.setRows(5);
        lot.setColumns(10);
        lot.setAutoAssignment(false);
        lot.setDiscountsEnabled(false);
        return lot;
    }

    // ── RF_09 – hour validation ────────────────────────────────────────────────

    @Test
    @DisplayName("Create throws when opening time is AFTER closing time")
    void createThrowsWhenOpeningAfterClosing() {
        ParkingLotRequest req = buildRequest(
                LocalTime.of(22, 0),   // opening: 10 PM
                LocalTime.of(6, 0));   // closing:  6 AM  ← earlier → invalid

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> parkingLotService.create(req),
                "Should reject a schedule where opening > closing"
        );

        assertTrue(ex.getMessage().contains("apertura"),
                "Error message should mention 'apertura' (opening time)");

        verify(parkingLotRepository, never()).save(any());
        verify(cellRepository, never()).saveAll(any());
    }

    // ── Create with cell generation (RF_01) ────────────────────────────────────

    @Test
    @DisplayName("Create generates rows × columns cells")
    void createGeneratesCells() {
        ParkingLotRequest req = buildRequest(LocalTime.of(6, 0), LocalTime.of(22, 0));
        ParkingLot savedLot = buildSavedLot(1L);

        when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(savedLot);

        ParkingLot result = parkingLotService.create(req);

        assertNotNull(result);
        verify(parkingLotRepository, times(1)).save(any(ParkingLot.class));

        ArgumentCaptor<List<Cell>> captor = ArgumentCaptor.captor();
        verify(cellRepository, times(1)).saveAll(captor.capture());

        List<Cell> generated = captor.getValue();
        assertEquals(50, generated.size(), "5 rows × 10 cols = 50 cells");
        Cell first = generated.getFirst();
        assertEquals(1, first.getRow().intValue());
        assertEquals(1, first.getCol().intValue());
        assertEquals("parking", first.getCellType());
        assertEquals("available", first.getStatus());
    }

    // ── Update ────────────────────────────────────────────────

    @Test
    @DisplayName("Update throws when the parqueadero does not exist")
    void updateThrowsWhenLotNotFound() {
        when(parkingLotRepository.findById(99L)).thenReturn(Optional.empty());

        ParkingLotRequest req = buildRequest(LocalTime.of(8, 0), LocalTime.of(20, 0));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> parkingLotService.update(99L, req),
                "Should throw when the ID does not exist"
        );

        assertTrue(ex.getMessage().contains("99"),
                "Error should mention the missing ID");
    }

    @Test
    @DisplayName("Update adds cells when dimensions increase")
    void updateAddsCellsWhenDimensionsIncrease() {
        ParkingLot existing = buildSavedLot(1L);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(existing);
        when(cellRepository.findByParkingLot(existing)).thenReturn(Collections.emptyList());

        ParkingLotRequest req = buildRequest(LocalTime.of(6, 0), LocalTime.of(22, 0));
        req.setRows(6);
        req.setColumns(12);

        parkingLotService.update(1L, req);

        ArgumentCaptor<List<Cell>> captor = ArgumentCaptor.captor();
        verify(cellRepository, times(1)).saveAll(captor.capture());
        assertEquals(72, captor.getValue().size());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Delete removes the parking lot when the ID exists")
    void deleteSucceedsWhenLotExists() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        doNothing().when(parkingLotRepository).deleteById(1L);

        assertDoesNotThrow(() -> parkingLotService.delete(1L));

        verify(parkingLotRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete throws when the parking lot does not exist")
    void deleteThrowsWhenLotNotFound() {
        when(parkingLotRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> parkingLotService.delete(99L),
                "Should throw when trying to delete a non-existent lot"
        );

        assertTrue(ex.getMessage().contains("eliminar"),
                "Error message should mention 'eliminar' (delete)");

        verify(parkingLotRepository, never()).deleteById(any());
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById returns the parking lot when the ID exists")
    void getByIdReturnsLot() {
        ParkingLot lot = buildSavedLot(1L);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));

        ParkingLot result = parkingLotService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById throws when the ID does not exist")
    void getByIdThrowsWhenNotFound() {
        when(parkingLotRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> parkingLotService.getById(404L),
                "Should throw when the lot is not found"
        );

        assertTrue(ex.getMessage().contains("404"),
                "Error should mention the missing ID");
    }

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll returns the complete list from the repository")
    void getAllReturnsRepositoryList() {
        List<ParkingLot> lots = List.of(buildSavedLot(1L), buildSavedLot(2L));
        when(parkingLotRepository.findAll()).thenReturn(lots);

        List<ParkingLot> result = parkingLotService.getAll();

        assertEquals(2, result.size(), "Should return exactly 2 lots");
        verify(parkingLotRepository, times(1)).findAll();
    }

    // ── toggleDiscountsEnabled ────────────────────────────────────────────────

    @Test
    @DisplayName("toggleDiscountsEnabled flips the flag")
    void toggleDiscountsEnabledFlipsFlag() {
        ParkingLot lot = buildSavedLot(1L);
        lot.setDiscountsEnabled(false);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(parkingLotRepository.save(any(ParkingLot.class))).thenAnswer(inv -> inv.getArgument(0));

        ParkingLot result = parkingLotService.toggleDiscountsEnabled(1L);

        assertTrue(result.getDiscountsEnabled());
    }
}
