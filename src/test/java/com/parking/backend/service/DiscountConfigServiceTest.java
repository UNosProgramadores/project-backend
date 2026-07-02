package com.parking.backend.service;

import com.parking.backend.dto.DiscountConfigRequest;
import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.DiscountConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountConfigServiceTest {

    @Mock
    private DiscountConfigRepository repository;

    @Mock
    private ParkingLotService parkingLotService;

    @InjectMocks
    private DiscountConfigService discountConfigService;

    private ParkingLot buildLot(Long id) {
        ParkingLot lot = new ParkingLot();
        lot.setId(id);
        lot.setName("Test Lot");
        return lot;
    }

    private DiscountConfig buildConfig(Long id, ParkingLot lot) {
        DiscountConfig config = new DiscountConfig();
        config.setId(id);
        config.setParkingLot(lot);
        config.setActive(true);
        config.setDiscountPercentage(BigDecimal.TEN);
        config.setMinExternalInvoice(new BigDecimal("50"));
        config.setMinVisits(5);
        return config;
    }

    private DiscountConfigRequest buildRequest() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setMinExternalInvoice(new BigDecimal("50"));
        req.setMinVisits(5);
        req.setDiscountPercentage(BigDecimal.TEN);
        req.setActive(true);
        return req;
    }

    @Test
    @DisplayName("getByParkingLot returns configs for a parking lot")
    void getByParkingLotReturnsConfigs() {
        ParkingLot lot = buildLot(1L);
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(repository.findByParkingLot(lot)).thenReturn(List.of(buildConfig(1L, lot)));

        List<DiscountConfig> result = discountConfigService.getByParkingLot(1L);

        assertEquals(1, result.size());
        verify(repository, times(1)).findByParkingLot(lot);
    }

    @Test
    @DisplayName("getById returns config when exists")
    void getByIdReturnsConfig() {
        ParkingLot lot = buildLot(1L);
        DiscountConfig config = buildConfig(1L, lot);
        when(repository.findById(1L)).thenReturn(Optional.of(config));

        DiscountConfig result = discountConfigService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById throws when not found")
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> discountConfigService.getById(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("create saves and returns a new discount config")
    void createSuccess() {
        ParkingLot lot = buildLot(1L);
        DiscountConfigRequest req = buildRequest();
        when(parkingLotService.getById(1L)).thenReturn(lot);
        when(repository.save(any(DiscountConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        DiscountConfig result = discountConfigService.create(1L, req);

        assertNotNull(result);
        assertEquals(lot, result.getParkingLot());
        assertEquals(req.getMinExternalInvoice(), result.getMinExternalInvoice());
        assertEquals(req.getMinVisits(), result.getMinVisits());
        assertEquals(req.getDiscountPercentage(), result.getDiscountPercentage());
        assertEquals(req.getActive(), result.getActive());
        assertNotNull(result.getStartDate());
        verify(repository, times(1)).save(any(DiscountConfig.class));
    }

    @Test
    @DisplayName("update versiones: cierra registro anterior y crea uno nuevo")
    void updateVersions() {
        ParkingLot lot = buildLot(1L);
        DiscountConfig existing = buildConfig(1L, lot);
        existing.setActive(true);
        DiscountConfigRequest req = buildRequest();
        req.setDiscountPercentage(new BigDecimal("15"));
        req.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(DiscountConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        DiscountConfig result = discountConfigService.update(1L, req);

        assertFalse(existing.getActive(), "Old record should be inactive");
        assertNotNull(existing.getEndDate(), "Old record should have end date");

        assertTrue(result.getActive());
        assertEquals(new BigDecimal("15"), result.getDiscountPercentage());
        assertNull(result.getEndDate());
        assertNotNull(result.getStartDate());
        verify(repository, times(2)).save(any(DiscountConfig.class));
    }

    @Test
    @DisplayName("delete removes config when exists")
    void deleteSuccess() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> discountConfigService.delete(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws when config not found")
    void deleteThrowsWhenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> discountConfigService.delete(99L));

        assertTrue(ex.getMessage().contains("eliminar"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("findActiveByParkingLot returns active config")
    void findActiveByParkingLotReturnsConfig() {
        ParkingLot lot = buildLot(1L);
        DiscountConfig config = buildConfig(1L, lot);
        when(repository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));

        Optional<DiscountConfig> result = discountConfigService.findActiveByParkingLot(lot);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }
}
