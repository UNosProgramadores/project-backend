package com.parking.backend.service;

import com.parking.backend.dto.RateRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Rate;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private RateRepository repository;

    @Mock
    private ParkingLotService parkingLotService;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @InjectMocks
    private RateService rateService;

    private ParkingLot buildLot() {
        ParkingLot lot = new ParkingLot();
        lot.setId(1L);
        return lot;
    }

    private VehicleType buildVehicleType() {
        VehicleType vt = new VehicleType();
        vt.setId(1L);
        vt.setName("car");
        return vt;
    }

    private Rate buildRate(Long id, ParkingLot lot, VehicleType vt) {
        Rate rate = new Rate();
        rate.setId(id);
        rate.setParkingLot(lot);
        rate.setVehicleType(vt);
        rate.setRateType("per_minute");
        rate.setCost(new BigDecimal("100"));
        rate.setActive(true);
        return rate;
    }

    private RateRequest buildRequest() {
        RateRequest req = new RateRequest();
        req.setVehicleTypeId(1L);
        req.setRateType("per_minute");
        req.setCost(new BigDecimal("150"));
        req.setActive(true);
        return req;
    }

    @Test
    @DisplayName("create saves rate")
    void createSuccess() {
        ParkingLot lot = buildLot();
        VehicleType vt = buildVehicleType();
        RateRequest req = buildRequest();

        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(vt));
        when(repository.save(any(Rate.class))).thenAnswer(inv -> inv.getArgument(0));

        Rate result = rateService.create(1L, req);

        assertNotNull(result);
        assertEquals(lot, result.getParkingLot());
        assertEquals(vt, result.getVehicleType());
        assertEquals("per_minute", result.getRateType());
        assertEquals(new BigDecimal("150"), result.getCost());
        assertTrue(result.getActive());
        verify(repository, times(1)).save(any(Rate.class));
    }

    @Test
    @DisplayName("update versiones: cierra registro anterior y crea uno nuevo")
    void updateVersions() {
        ParkingLot lot = buildLot();
        VehicleType vt = buildVehicleType();
        Rate existing = buildRate(1L, lot, vt);
        RateRequest req = buildRequest();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(vt));
        when(repository.save(any(Rate.class))).thenAnswer(inv -> inv.getArgument(0));

        Rate result = rateService.update(1L, 1L, req);

        assertFalse(existing.getActive(), "Old record should be inactive");
        assertNotNull(existing.getEndDate(), "Old record should have end date");

        assertTrue(result.getActive());
        assertEquals(new BigDecimal("150"), result.getCost());
        assertNull(result.getEndDate());
        assertNotNull(result.getStartDate());
        verify(repository, times(2)).save(any(Rate.class));
    }

    @Test
    @DisplayName("delete removes rate when exists")
    void deleteSuccess() {
        ParkingLot lot = buildLot();
        VehicleType vt = buildVehicleType();
        Rate rate = buildRate(1L, lot, vt);
        when(repository.findById(1L)).thenReturn(Optional.of(rate));

        assertDoesNotThrow(() -> rateService.delete(1L, 1L));
        verify(repository, times(1)).delete(rate);
    }

    @Test
    @DisplayName("delete throws when rate not found")
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rateService.delete(99L, 1L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("getById returns rate when exists")
    void getByIdReturnsRate() {
        ParkingLot lot = buildLot();
        VehicleType vt = buildVehicleType();
        Rate rate = buildRate(1L, lot, vt);
        when(repository.findById(1L)).thenReturn(Optional.of(rate));

        Rate result = rateService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById throws when not found")
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rateService.getById(99L, 1L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("getById throws when rate belongs to different parking lot")
    void getByIdWithWrongParkingLotThrows() {
        ParkingLot lotA = new ParkingLot();
        lotA.setId(1L);
        ParkingLot lotB = new ParkingLot();
        lotB.setId(99L);
        VehicleType vt = buildVehicleType();
        Rate rate = buildRate(1L, lotA, vt);
        when(repository.findById(1L)).thenReturn(Optional.of(rate));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rateService.getById(1L, 99L));

        assertTrue(ex.getMessage().contains("no pertenece"));
    }

    @Test
    @DisplayName("update throws when rate belongs to different parking lot")
    void updateWithWrongParkingLotThrows() {
        ParkingLot lotA = new ParkingLot();
        lotA.setId(1L);
        VehicleType vt = buildVehicleType();
        Rate rate = buildRate(1L, lotA, vt);
        RateRequest req = buildRequest();

        when(repository.findById(1L)).thenReturn(Optional.of(rate));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rateService.update(1L, 99L, req));

        assertTrue(ex.getMessage().contains("no pertenece"));
        verify(repository, never()).save(any(Rate.class));
    }

    @Test
    @DisplayName("delete throws when rate belongs to different parking lot")
    void deleteWithWrongParkingLotThrows() {
        ParkingLot lotA = new ParkingLot();
        lotA.setId(1L);
        VehicleType vt = buildVehicleType();
        Rate rate = buildRate(1L, lotA, vt);
        when(repository.findById(1L)).thenReturn(Optional.of(rate));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rateService.delete(1L, 99L));

        assertTrue(ex.getMessage().contains("no pertenece"));
        verify(repository, never()).delete(any());
    }
}
