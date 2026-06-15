package com.parking.backend.service;

import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import com.parking.backend.repository.ParkingLotRepository;

import com.parking.backend.dto.VehicleEntryRequest;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Vehicle;

import java.time.LocalDateTime;

@Service
    public class EntryRecordService {
    private final ParkingLotRepository parkingLotRepository;
    private final EntryRecordRepository entryRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final CellRepository cellRepository;
    private final RateRepository rateRepository;

    public EntryRecordService(ParkingLotRepository parkingLotRepository,
                              EntryRecordRepository entryRecordRepository,
                              VehicleRepository vehicleRepository,
                              CellRepository cellRepository,
                              RateRepository rateRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.entryRecordRepository = entryRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.cellRepository = cellRepository;
        this.rateRepository = rateRepository;
    }

    public EntryRecord registerEntry(VehicleEntryRequest request) {

        ParkingLot parkingLot = parkingLotRepository.findById(
                request.getParkingLotId()
        ).orElseThrow(() ->
                new RuntimeException("Parking lot not found")
        );

        Vehicle vehicle;

        if (request.getPlate() != null &&
                !request.getPlate().isBlank()) {

            vehicle = vehicleRepository.findByPlate(
                    request.getPlate()
            ).orElseThrow(() ->
                    new RuntimeException("Vehicle not found")
            );

        } else if (request.getBikeRegistration() != null &&
                !request.getBikeRegistration().isBlank()) {

            vehicle = vehicleRepository.findByBikeRegistration(
                    request.getBikeRegistration()
            ).orElseThrow(() ->
                    new RuntimeException("Vehicle not found")
            );

        } else {

            throw new RuntimeException(
                    "Plate or bike registration is required"
            );
        }

        entryRecordRepository.findByVehicleAndStatus(
                vehicle,
                "active"
        ).ifPresent(record -> {
            throw new RuntimeException(
                    "Vehicle already inside parking lot"
            );
        });

        Cell cell = cellRepository
                .findFirstByParkingLotAndVehicleTypeAndStatus(
                        parkingLot,
                        vehicle.getVehicleType(),
                        "available"
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No available cell found"
                        )
                );

        EntryRecord entryRecord = new EntryRecord();

        entryRecord.setVehicle(vehicle);
        entryRecord.setCell(cell);
        entryRecord.setEntryTime(LocalDateTime.now());
        entryRecord.setStatus("active");

        cell.setStatus("occupied");

        cellRepository.save(cell);

        return entryRecordRepository.save(entryRecord);
    }
}