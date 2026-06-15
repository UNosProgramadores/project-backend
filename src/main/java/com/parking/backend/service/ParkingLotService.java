package com.parking.backend.service;

import com.parking.backend.dto.ParkingLotRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<ParkingLot> getAll() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot getById(Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parqueadero no encontrado con ID: " + id));
    }

    @Transactional
    public ParkingLot create(ParkingLotRequest request) {
        validateHours(request);

        ParkingLot parkingLot = new ParkingLot();
        updateEntityFromRequest(parkingLot, request);

        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public ParkingLot update(Long id, ParkingLotRequest request) {
        validateHours(request);

        ParkingLot parkingLot = getById(id);
        updateEntityFromRequest(parkingLot, request);

        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public void delete(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Parqueadero no encontrado con ID: " + id);
        }
        parkingLotRepository.deleteById(id);
    }

    private void validateHours(ParkingLotRequest request) {
        if (request.getOpeningTime().isAfter(request.getClosingTime()) ||
            request.getOpeningTime().equals(request.getClosingTime())) {
            throw new RuntimeException("La hora de apertura debe ser anterior a la hora de cierre");
        }
    }

    private void updateEntityFromRequest(ParkingLot entity, ParkingLotRequest request) {
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setOpeningTime(request.getOpeningTime());
        entity.setClosingTime(request.getClosingTime());
        entity.setRows(request.getRows());
        entity.setColumns(request.getColumns());
        entity.setAutoAssignment(request.getAutoAssignment());
        entity.setDiscountsEnabled(request.getDiscountsEnabled());
    }
}
