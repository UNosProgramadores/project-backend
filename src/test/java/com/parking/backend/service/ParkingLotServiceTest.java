package com.parking.backend.service;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

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
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Delete removes the parqueadero when the ID exists")
    void deleteSucceedsWhenLotExists() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        doNothing().when(parkingLotRepository).deleteById(1L);

        assertDoesNotThrow(() -> parkingLotService.delete(1L));

        verify(parkingLotRepository, times(1)).deleteById(1L);
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


}