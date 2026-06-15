package com.parking.backend.repository;

import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntryRecordRepository extends JpaRepository<EntryRecord, Long> {

    Optional <EntryRecord> findByVehicleAndStatus(
            Vehicle vehicle,
            String status
    );

    List<EntryRecord> findByStatus(String status);
}