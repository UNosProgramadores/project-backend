package com.parking.backend.repository;

import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByEntryRecord_Vehicle_OwnerAndEntryRecord_Cell_ParkingLotAndEntryRecord_StatusAndExternalInvoiceRefIsNotNull(User user, ParkingLot parkingLot, String status);
}
