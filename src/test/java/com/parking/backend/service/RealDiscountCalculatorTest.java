package com.parking.backend.service;

import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.User;
import com.parking.backend.repository.DiscountConfigRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealDiscountCalculatorTest {

    @Mock
    private DiscountConfigRepository discountConfigRepository;

    @Mock
    private EntryRecordRepository entryRecordRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RealDiscountCalculator calculator;

    private ParkingLot buildLot(boolean discountsEnabled) {
        ParkingLot lot = new ParkingLot();
        lot.setId(1L);
        lot.setDiscountsEnabled(discountsEnabled);
        return lot;
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    private DiscountConfig buildConfig(BigDecimal minInvoice, Integer minVisits, BigDecimal percentage) {
        DiscountConfig config = new DiscountConfig();
        config.setId(1L);
        config.setActive(true);
        config.setMinExternalInvoice(minInvoice);
        config.setMinVisits(minVisits);
        config.setDiscountPercentage(percentage);
        return config;
    }

    @Test
    @DisplayName("Returns ZERO when discounts are disabled")
    void returnsZeroWhenDiscountsDisabled() {
        ParkingLot lot = buildLot(false);
        User user = buildUser();

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("100"));

        assertEquals(BigDecimal.ZERO, result);
        verifyNoInteractions(discountConfigRepository);
    }

    @Test
    @DisplayName("Returns ZERO when no active discount config exists")
    void returnsZeroWhenNoActiveConfig() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.empty());

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("100"));

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Applies invoice-based discount when user has external invoice")
    void appliesInvoiceDiscount() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        DiscountConfig config = buildConfig(new BigDecimal("50"), null, new BigDecimal("10"));
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));
        when(paymentRepository.existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(user, lot, "completed")).thenReturn(true);

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("200"));

        assertEquals(new BigDecimal("20"), result); // 200 * 10% = 20
    }

    @Test
    @DisplayName("Applies visit-based discount when user has enough visits")
    void appliesVisitDiscount() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        DiscountConfig config = buildConfig(null, 3, new BigDecimal("10"));
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));
        when(entryRecordRepository.countByVehicle_OwnerAndCell_ParkingLotAndStatus(user, lot, "completed")).thenReturn(5L);

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("100"));

        assertEquals(new BigDecimal("10"), result); // 100 * 10% = 10
    }

    @Test
    @DisplayName("Returns ZERO when user does not qualify for any discount")
    void returnsZeroWhenNotQualified() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        DiscountConfig config = buildConfig(new BigDecimal("50"), 3, new BigDecimal("10"));
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));
        when(paymentRepository.existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(user, lot, "completed")).thenReturn(false);
        when(entryRecordRepository.countByVehicle_OwnerAndCell_ParkingLotAndStatus(user, lot, "completed")).thenReturn(1L);

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("100"));

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Invoice discount takes priority over visit discount")
    void invoiceDiscountTakesPriority() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        DiscountConfig config = buildConfig(new BigDecimal("50"), 3, new BigDecimal("10"));
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));
        when(paymentRepository.existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(user, lot, "completed")).thenReturn(true);

        BigDecimal result = calculator.calculateDiscount(lot, user, new BigDecimal("100"));

        assertEquals(new BigDecimal("10"), result);
        verify(entryRecordRepository, never()).countByVehicle_OwnerAndCell_ParkingLotAndStatus(any(), any(), any());
    }

    @Test
    @DisplayName("Handles zero base amount correctly")
    void handlesZeroBaseAmount() {
        ParkingLot lot = buildLot(true);
        User user = buildUser();
        DiscountConfig config = buildConfig(new BigDecimal("50"), 3, new BigDecimal("10"));
        when(discountConfigRepository.findByParkingLotAndActiveTrue(lot)).thenReturn(Optional.of(config));
        when(paymentRepository.existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(user, lot, "completed")).thenReturn(true);

        BigDecimal result = calculator.calculateDiscount(lot, user, BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, result);
    }
}
