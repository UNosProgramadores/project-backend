package com.parking.backend.service;

import com.parking.backend.dto.ReportResponse;
import com.parking.backend.dto.ReportResponse.StaffActivity;
import com.parking.backend.dto.ReportResponse.VehicleTypeCount;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ReportResponse generateReport(Long parkingLotId, LocalDate startDate, LocalDate endDate) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parqueadero no encontrado con ID: " + parkingLotId));

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        BigDecimal totalRevenue = paymentRepository.sumTotalPaidByParkingLotAndDateRange(parkingLotId, start, end);

        List<Object[]> entryRaw = entryRecordRepository.countEntriesByVehicleTypeAndDateRange(parkingLotId, start, end);
        List<Object[]> exitRaw = entryRecordRepository.countExitsByVehicleTypeAndDateRange(parkingLotId, start, end);

        List<VehicleTypeCount> entriesByType = mapCounts(entryRaw);
        List<VehicleTypeCount> exitsByType = mapCounts(exitRaw);

        List<Object[]> entryStaffRaw = entryRecordRepository.countEntriesByStaff(parkingLotId, start, end);
        List<Object[]> exitStaffRaw = entryRecordRepository.countExitsByStaff(parkingLotId, start, end);

        List<StaffActivity> staffActivity = mergeStaffActivity(entryStaffRaw, exitStaffRaw);

        ReportResponse res = new ReportResponse();
        res.setParkingLotId(parkingLot.getId());
        res.setParkingLotName(parkingLot.getName());
        res.setPeriod("");
        res.setReferenceDate(startDate.toString());
        res.setStartDate(start.toString());
        res.setEndDate(end.toString());
        res.setTotalRevenue(totalRevenue);
        res.setTotalEntries(entriesByType.stream().mapToLong(VehicleTypeCount::getCount).sum());
        res.setTotalExits(exitsByType.stream().mapToLong(VehicleTypeCount::getCount).sum());
        res.setEntriesByVehicleType(entriesByType);
        res.setExitsByVehicleType(exitsByType);
        res.setStaffActivity(staffActivity);

        return res;
    }

    private List<StaffActivity> mergeStaffActivity(List<Object[]> entryStaff, List<Object[]> exitStaff) {
        Map<Long, StaffActivity> map = new HashMap<>();

        for (Object[] row : entryStaff) {
            Long id = (Long) row[0];
            String name = (String) row[1];
            long count = (Long) row[2];
            map.put(id, new StaffActivity(id, name, count, 0));
        }

        for (Object[] row : exitStaff) {
            Long id = (Long) row[0];
            String name = (String) row[1];
            long count = (Long) row[2];
            map.computeIfAbsent(id, k -> new StaffActivity(id, name, 0, 0)).setExitsRecorded(count);
        }

        return new ArrayList<>(map.values());
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
