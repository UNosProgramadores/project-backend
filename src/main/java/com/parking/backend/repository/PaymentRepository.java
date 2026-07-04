package com.parking.backend.repository;

import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(User user, ParkingLot parkingLot, String status);

    @Query("SELECT COALESCE(SUM(p.totalPaid), 0) FROM Payment p " +
           "WHERE p.entryRecord.cell.parkingLot.id = :parkingLotId " +
           "AND p.paymentDate >= :start AND p.paymentDate < :end")
    BigDecimal sumTotalPaidByParkingLotAndDateRange(@Param("parkingLotId") Long parkingLotId,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);
}
