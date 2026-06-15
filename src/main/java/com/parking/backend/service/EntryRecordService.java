package com.parking.backend.service;

import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class EntryRecordService {

    private final EntryRecordRepository entryRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final CellRepository cellRepository;
    private final RateRepository rateRepository;

    public EntryRecordService(
            EntryRecordRepository entryRecordRepository,
            VehicleRepository vehicleRepository,
            CellRepository cellRepository,
            RateRepository rateRepository
    ) {
        this.entryRecordRepository = entryRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.cellRepository = cellRepository;
        this.rateRepository = rateRepository;
    }
}