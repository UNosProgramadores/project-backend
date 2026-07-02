package com.parking.backend.service;

import com.parking.backend.dto.DiscountConfigRequest;
import com.parking.backend.entity.DiscountConfig;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.repository.DiscountConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountConfigService {

    private final DiscountConfigRepository repository;
    private final ParkingLotService parkingLotService;

    public DiscountConfigService(DiscountConfigRepository repository, ParkingLotService parkingLotService) {
        this.repository = repository;
        this.parkingLotService = parkingLotService;
    }

    public List<DiscountConfig> getByParkingLot(Long parkingLotId) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        return repository.findByParkingLot(lot);
    }

    public DiscountConfig getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de descuento no encontrada con ID: " + id));
    }

    @Transactional
    public DiscountConfig create(Long parkingLotId, DiscountConfigRequest request) {
        ParkingLot lot = parkingLotService.getById(parkingLotId);
        DiscountConfig config = new DiscountConfig();
        applyRequest(config, request, lot);
        return repository.save(config);
    }

    @Transactional
    public DiscountConfig update(Long id, DiscountConfigRequest request) {
        DiscountConfig existing = getById(id);
        // RF_08: close the active record and create a new one
        existing.setActive(false);
        existing.setEndDate(LocalDateTime.now());
        repository.save(existing);

        DiscountConfig config = new DiscountConfig();
        applyRequest(config, request, existing.getParkingLot());
        return repository.save(config);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Configuración de descuento no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    public Optional<DiscountConfig> findActiveByParkingLot(ParkingLot parkingLot) {
        return repository.findByParkingLotAndActiveTrue(parkingLot);
    }

    private void applyRequest(DiscountConfig entity, DiscountConfigRequest request, ParkingLot lot) {
        entity.setParkingLot(lot);
        entity.setMinExternalInvoice(request.getMinExternalInvoice());
        entity.setMinVisits(request.getMinVisits());
        entity.setDiscountPercentage(request.getDiscountPercentage());
        entity.setActive(request.getActive());
        entity.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());
        entity.setEndDate(request.getEndDate());
    }
}
