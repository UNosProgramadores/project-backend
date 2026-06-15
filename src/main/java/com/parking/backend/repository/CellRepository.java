package com.parking.backend.repository;

import com.parking.backend.entity.Cell;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CellRepository extends JpaRepository<Cell, Long> {

    Optional<Cell> findFirstByParkingLotAndVehicleTypeAndStatus(
            ParkingLot parkingLot,
            VehicleType vehicleType,
            String status
    );
}