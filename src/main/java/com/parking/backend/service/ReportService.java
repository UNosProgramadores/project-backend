package com.parking.backend.service;

import com.parking.backend.dto.ReportResponse;
import com.parking.backend.dto.ReportResponse.VehicleTypeCount;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final EntryRecordRepository entryRecordRepository;
    private final ParkingLotRepository parkingLotRepository;

    public ReportService(PaymentRepository paymentRepository,
                         EntryRecordRepository entryRecordRepository,
                         ParkingLotRepository parkingLotRepository) {
        this.paymentRepository = paymentRepository;
        this.entryRecordRepository = entryRecordRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    /**
     * Generates a report for the given parking lot and period.
     *
     * Period boundaries (ISO-like):
     * - day:   [date 00:00, next day 00:00)
     * - week:  [Monday 00:00 of the ISO week containing date, next Monday 00:00)
     * - month: [1st of the month 00:00, 1st of next month 00:00)
     *
     * Using half-open intervals [start, end) ensures that adding a day/month/week
     * always produces a clean non-overlapping boundary without 23:59:59 edge cases.
     */
    public ReportResponse generateReport(Long parkingLotId, String period, LocalDate referenceDate) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found with ID: " + parkingLotId));

        LocalDateTime start = computeStart(referenceDate, period);
        LocalDateTime end = computeEnd(referenceDate, period);

        BigDecimal totalRevenue = paymentRepository.sumTotalPaidByParkingLotAndDateRange(parkingLotId, start, end);

        List<Object[]> entryRaw = entryRecordRepository.countEntriesByVehicleTypeAndDateRange(parkingLotId, start, end);
        List<Object[]> exitRaw = entryRecordRepository.countExitsByVehicleTypeAndDateRange(parkingLotId, start, end);

        List<VehicleTypeCount> entriesByType = mapCounts(entryRaw);
        List<VehicleTypeCount> exitsByType = mapCounts(exitRaw);

        ReportResponse res = new ReportResponse();
        res.setParkingLotId(parkingLot.getId());
        res.setParkingLotName(parkingLot.getName());
        res.setPeriod(period);
        res.setReferenceDate(referenceDate.toString());
        res.setStartDate(start.toString());
        res.setEndDate(end.toString());
        res.setTotalRevenue(totalRevenue);
        res.setTotalEntries(entriesByType.stream().mapToLong(VehicleTypeCount::getCount).sum());
        res.setTotalExits(exitsByType.stream().mapToLong(VehicleTypeCount::getCount).sum());
        res.setEntriesByVehicleType(entriesByType);
        res.setExitsByVehicleType(exitsByType);

        return res;
    }

    private LocalDateTime computeStart(LocalDate date, String period) {
        return switch (period) {
            case "day" -> date.atStartOfDay();
            case "week" -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            case "month" -> date.withDayOfMonth(1).atStartOfDay();
            default -> throw new RuntimeException("Invalid period: " + period + ". Must be 'day', 'week', or 'month'");
        };
    }

    private LocalDateTime computeEnd(LocalDate date, String period) {
        LocalDate endDate = switch (period) {
            case "day" -> date.plusDays(1);
            case "week" -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(1);
            case "month" -> date.withDayOfMonth(1).plusMonths(1);
            default -> throw new RuntimeException("Invalid period: " + period);
        };
        return endDate.atStartOfDay();
    }

    private List<VehicleTypeCount> mapCounts(List<Object[]> raw) {
        List<VehicleTypeCount> result = new ArrayList<>();
        for (Object[] row : raw) {
            String type = (String) row[0];
            long count = (Long) row[1];
            result.add(new VehicleTypeCount(type, count));
        }
        return result;
    }
}
