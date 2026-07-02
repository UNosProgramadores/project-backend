package com.parking.backend.repository;

import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.User;
import com.parking.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EntryRecordRepository extends JpaRepository<EntryRecord, Long> {

    Optional<EntryRecord> findByVehicleAndStatus(
            Vehicle vehicle,
            String status
    );

    List<EntryRecord> findByStatus(String status);

    @Query("SELECT COUNT(e) FROM EntryRecord e WHERE e.vehicle.owner = :user AND e.cell.parkingLot = :parkingLot AND e.status = 'completed'")
    long countCompletedByOwnerAndParkingLot(@Param("user") User user, @Param("parkingLot") ParkingLot parkingLot);
}