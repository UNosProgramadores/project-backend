package com.parking.backend.repository;

import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface CellRepository extends JpaRepository<Cell, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cell> findFirstByParkingLotAndVehicleTypeAndStatusAndActiveTrue(
            ParkingLot parkingLot,
            VehicleType vehicleType,
            String status
    );

    List<Cell> findByParkingLot(ParkingLot parkingLot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cell> findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
            ParkingLot parkingLot,
            VehicleType vehicleType,
            String status,
            Boolean reservedForStaff
    );

    Optional<Cell> findByIdAndParkingLotAndActiveTrue(Long id, ParkingLot parkingLot);
}