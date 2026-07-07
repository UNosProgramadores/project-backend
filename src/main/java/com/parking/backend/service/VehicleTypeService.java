package com.parking.backend.service;

import com.parking.backend.dto.VehicleTypeResponse;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleTypeService(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public List<VehicleTypeResponse> getAll() {
        return vehicleTypeRepository.findAll()
                .stream()
                .map(vt -> new VehicleTypeResponse(vt.getId(), vt.getName(), vt.getRequiresPlate()))
                .toList();
    }
}
