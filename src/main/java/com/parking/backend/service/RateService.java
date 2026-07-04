package com.parking.backend.service;

import com.parking.backend.dto.RateRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Rate;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RateService {

    private final RateRepository repository;
    private final ParkingLotService parkingLotService;
    private final VehicleTypeRepository vehicleTypeRepository;

    public RateService(RateRepository repository,
                       ParkingLotService parkingLotService,
                       VehicleTypeRepository vehicleTypeRepository) {
        this.repository = repository;
        this.parkingLotService = parkingLotService;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public List<Rate> getByParkingLot(Long parkingLotId) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        return repository.findByParkingLot(lot);
    }

    public Rate getById(Long id, Long parkingLotId) {
        Rate rate = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + id));
        if (!rate.getParkingLot().getId().equals(parkingLotId)) {
            throw new RuntimeException("La tarifa no pertenece a este parqueadero");
        }
        return rate;
    }

    @Transactional
    public Rate create(Long parkingLotId, RateRequest request) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        Rate rate = new Rate();
        applyRequest(rate, request, lot);
        rate.setStartDate(LocalDateTime.now());
        return repository.save(rate);
    }

    @Transactional
    public Rate update(Long id, Long parkingLotId, RateRequest request) {
        Rate existing = getById(id, parkingLotId);
        // RF_08: close the active record and create a new one
        existing.setActive(false);
        existing.setEndDate(LocalDateTime.now());
        repository.save(existing);

        Rate rate = new Rate();
        applyRequest(rate, request, existing.getParkingLot());
        rate.setStartDate(LocalDateTime.now());
        return repository.save(rate);
    }

    @Transactional
    public void delete(Long id, Long parkingLotId) {
        Rate rate = getById(id, parkingLotId);
        repository.delete(rate);
    }

    private void applyRequest(Rate entity, RateRequest request, ParkingLot lot) {
        VehicleType vt = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con ID: " + request.getVehicleTypeId()));
        entity.setParkingLot(lot);
        entity.setVehicleType(vt);
        entity.setRateType(request.getRateType());
        entity.setCost(request.getCost());
        entity.setActive(request.getActive() != null ? request.getActive() : true);
    }
}
