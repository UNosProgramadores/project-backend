package com.parking.backend.service;

import com.parking.backend.dto.ReportResponse;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EntryRecordRepository entryRecordRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ReportService reportService;

    @Captor
    private ArgumentCaptor<LocalDateTime> startCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> endCaptor;

    private ParkingLot buildLot(Long id, String name) {
        ParkingLot lot = new ParkingLot();
        lot.setId(id);
        lot.setName(name);
        return lot;
    }

    private List<Object[]> listOfArrays(Object[]... arrays) {
        List<Object[]> result = new ArrayList<>();
        for (Object[] a : arrays) result.add(a);
        return result;
    }

    @Test
    @DisplayName("Day report returns correct revenue and counts for known data")
    void dayReportWithKnownData() {
        ParkingLot lot = buildLot(1L, "ParKing Downtown");
        LocalDate refDate = LocalDate.of(2026, 7, 4);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any()))
                .thenReturn(new BigDecimal("1250.00"));
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any()))
                .thenReturn(listOfArrays(
                        new Object[]{"car", 10L},
                        new Object[]{"motorcycle", 3L}
                ));
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any()))
                .thenReturn(listOfArrays(
                        new Object[]{"car", 8L},
                        new Object[]{"motorcycle", 2L}
                ));

        ReportResponse response = reportService.generateReport(1L, "day", refDate);

        assertEquals(1L, response.getParkingLotId());
        assertEquals("ParKing Downtown", response.getParkingLotName());
        assertEquals("day", response.getPeriod());
        assertEquals("2026-07-04", response.getReferenceDate());
        assertEquals(new BigDecimal("1250.00"), response.getTotalRevenue());
        assertEquals(13, response.getTotalEntries());
        assertEquals(10, response.getTotalExits());
        assertEquals(2, response.getEntriesByVehicleType().size());
        assertEquals("car", response.getEntriesByVehicleType().get(0).getVehicleType());
        assertEquals(10, response.getEntriesByVehicleType().get(0).getCount());
        assertEquals("motorcycle", response.getEntriesByVehicleType().get(1).getVehicleType());
        assertEquals(3, response.getEntriesByVehicleType().get(1).getCount());
        assertEquals(2, response.getExitsByVehicleType().size());
        assertEquals("car", response.getExitsByVehicleType().get(0).getVehicleType());
        assertEquals(8, response.getExitsByVehicleType().get(0).getCount());
    }

    @Test
    @DisplayName("Day report boundaries are [date 00:00, next-day 00:00)")
    void dayReportBoundaries() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        LocalDate refDate = LocalDate.of(2026, 7, 4);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        reportService.generateReport(1L, "day", refDate);

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), startCaptor.capture(), endCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 7, 4, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2026, 7, 5, 0, 0), endCaptor.getValue());
    }

    @Test
    @DisplayName("Week report boundaries are Monday 00:00 to next Monday 00:00 ISO")
    void weekReportBoundaries() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        LocalDate refDate = LocalDate.of(2026, 7, 1);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        reportService.generateReport(1L, "week", refDate);

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), startCaptor.capture(), endCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 6, 29, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2026, 7, 6, 0, 0), endCaptor.getValue());
    }

    @Test
    @DisplayName("Week report from Monday includes that day (edge case: first day of period)")
    void weekReportOnMondayBoundary() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        LocalDate refDate = LocalDate.of(2026, 6, 29);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        reportService.generateReport(1L, "week", refDate);

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), startCaptor.capture(), endCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 6, 29, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2026, 7, 6, 0, 0), endCaptor.getValue());
    }

    @Test
    @DisplayName("Month report boundaries are 1st 00:00 to 1st of next month 00:00")
    void monthReportBoundaries() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        LocalDate refDate = LocalDate.of(2026, 7, 15);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        reportService.generateReport(1L, "month", refDate);

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), startCaptor.capture(), endCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 7, 1, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2026, 8, 1, 0, 0), endCaptor.getValue());
    }

    @Test
    @DisplayName("Month report on December boundary rolls to next year correctly")
    void monthReportDecemberBoundary() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        LocalDate refDate = LocalDate.of(2026, 12, 10);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        reportService.generateReport(1L, "month", refDate);

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), startCaptor.capture(), endCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 12, 1, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2027, 1, 1, 0, 0), endCaptor.getValue());
    }

    @Test
    @DisplayName("Empty period returns zeros, not error")
    void emptyPeriodReturnsZeros() {
        ParkingLot lot = buildLot(1L, "ParKing Downtown");
        LocalDate refDate = LocalDate.of(2026, 7, 4);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());

        ReportResponse response = reportService.generateReport(1L, "day", refDate);

        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
        assertEquals(0, response.getTotalEntries());
        assertEquals(0, response.getTotalExits());
        assertTrue(response.getEntriesByVehicleType().isEmpty());
        assertTrue(response.getExitsByVehicleType().isEmpty());
    }

    @Test
    @DisplayName("Report filters correctly by parkingLotId (no data mixing)")
    void reportFiltersByParkingLotId() {
        ParkingLot lot1 = buildLot(1L, "Lot A");
        LocalDate refDate = LocalDate.of(2026, 7, 4);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot1));
        when(paymentRepository.sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any())).thenReturn(new BigDecimal("500.00"));
        when(entryRecordRepository.countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any()))
                .thenReturn(listOfArrays(new Object[]{"car", 5L}));
        when(entryRecordRepository.countExitsByVehicleTypeAndDateRange(eq(1L), any(), any()))
                .thenReturn(listOfArrays(new Object[]{"car", 3L}));

        ReportResponse response = reportService.generateReport(1L, "day", refDate);

        assertEquals(1L, response.getParkingLotId());
        assertEquals(new BigDecimal("500.00"), response.getTotalRevenue());
        assertEquals(5, response.getTotalEntries());

        verify(paymentRepository).sumTotalPaidByParkingLotAndDateRange(eq(1L), any(), any());
        verify(entryRecordRepository).countEntriesByVehicleTypeAndDateRange(eq(1L), any(), any());
        verify(entryRecordRepository).countExitsByVehicleTypeAndDateRange(eq(1L), any(), any());

        verify(paymentRepository, never()).sumTotalPaidByParkingLotAndDateRange(eq(2L), any(), any());
    }

    @Test
    @DisplayName("Report for non-existent parking lot throws exception")
    void reportForNonExistentLot() {
        when(parkingLotRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reportService.generateReport(99L, "day", LocalDate.of(2026, 7, 4)));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("Invalid period string throws exception")
    void invalidPeriodThrows() {
        ParkingLot lot = buildLot(1L, "Test Lot");
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reportService.generateReport(1L, "year", LocalDate.of(2026, 7, 4)));

        assertTrue(ex.getMessage().contains("Invalid period"));
    }
}
