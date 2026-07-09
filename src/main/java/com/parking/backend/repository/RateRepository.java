package com.parking.backend.repository;

import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Rate;
import com.parking.backend.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RateRepository extends JpaRepository<Rate, Long> {

    List<Rate> findByParkingLotAndVehicleTypeAndActive(
            ParkingLot parkingLot,
            VehicleType vehicleType,
            Boolean active
    );

    List<Rate> findByParkingLot(ParkingLot parkingLot);
}