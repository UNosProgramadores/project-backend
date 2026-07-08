package com.parking.backend.service;

import com.parking.backend.dto.ClaimVehicleRequest;
import com.parking.backend.entity.User;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public Vehicle claimVehicle(ClaimVehicleRequest request) {
        Vehicle vehicle = findVehicle(request);
        if (vehicle == null) {
            throw new RuntimeException("Vehículo no encontrado. Solo se pueden reclamar vehículos existentes que hayan ingresado al parqueadero.");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Usuario autenticado no encontrado");
        }

        if (vehicle.getOwner() != null && !vehicle.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Este vehículo ya está asociado a otro usuario");
        }

        vehicle.setOwner(currentUser);
        return vehicleRepository.save(vehicle);
    }

    private Vehicle findVehicle(ClaimVehicleRequest request) {
        if (request.getPlate() != null && !request.getPlate().isBlank()) {
            return vehicleRepository.findByPlate(request.getPlate()).orElse(null);
        }
        if (request.getBikeRegistration() != null && !request.getBikeRegistration().isBlank()) {
            return vehicleRepository.findByBikeRegistration(request.getBikeRegistration()).orElse(null);
        }
        throw new RuntimeException("Placa o registro de bicicleta es requerido");
    }

    public List<Vehicle> getMyVehicles() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Usuario autenticado no encontrado");
        }
        return vehicleRepository.findByOwnerId(currentUser.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = (String) authentication.getPrincipal();
        return userRepository.findByUsername(username).orElse(null);
    }
}
