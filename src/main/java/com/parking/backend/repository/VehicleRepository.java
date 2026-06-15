package com.parking.backend.repository;

import com.parking.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlate(String plate);

    Optional<Vehicle> findByBikeRegistration(String bikeRegistration);
}