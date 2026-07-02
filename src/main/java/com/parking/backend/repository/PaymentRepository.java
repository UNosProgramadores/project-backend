package com.parking.backend.repository;

import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.entryRecord.vehicle.owner = :user AND p.entryRecord.cell.parkingLot = :parkingLot AND p.entryRecord.status = 'completed' AND p.externalInvoiceRef IS NOT NULL")
    boolean existsCompletedWithInvoiceRefByUserAndParkingLot(@Param("user") User user, @Param("parkingLot") ParkingLot parkingLot);
}
