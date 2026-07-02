package com.parking.backend.repository;

import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountConfigRepository extends JpaRepository<DiscountConfig, Long> {

    Optional<DiscountConfig> findByParkingLotAndActiveTrue(ParkingLot parkingLot);

    List<DiscountConfig> findByParkingLot(ParkingLot parkingLot);
}
