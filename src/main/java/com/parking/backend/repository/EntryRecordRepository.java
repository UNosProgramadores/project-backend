package com.parking.backend.repository;

import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.User;
import com.parking.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EntryRecordRepository extends JpaRepository<EntryRecord, Long> {

    Optional<EntryRecord> findByVehicleAndStatus(Vehicle vehicle, String status);

    List<EntryRecord> findByStatus(String status);

    long countByVehicle_OwnerAndCell_ParkingLotAndStatus(User user, ParkingLot parkingLot, String status);

    List<EntryRecord> findByCell_ParkingLot_IdAndStatusOrderByEntryTimeDesc(Long parkingLotId, String status);

    @Query("SELECT er.vehicle.vehicleType.name, COUNT(er) FROM EntryRecord er " +
           "WHERE er.cell.parkingLot.id = :parkingLotId " +
           "AND er.entryTime >= :start AND er.entryTime < :end " +
           "GROUP BY er.vehicle.vehicleType.name")
    List<Object[]> countEntriesByVehicleTypeAndDateRange(@Param("parkingLotId") Long parkingLotId,
                                                          @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    @Query("SELECT er.vehicle.vehicleType.name, COUNT(er) FROM EntryRecord er " +
           "WHERE er.cell.parkingLot.id = :parkingLotId " +
           "AND er.exitTime >= :start AND er.exitTime < :end " +
           "AND er.status = 'completed' " +
           "GROUP BY er.vehicle.vehicleType.name")
    List<Object[]> countExitsByVehicleTypeAndDateRange(@Param("parkingLotId") Long parkingLotId,
                                                        @Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);
}