package com.parking.backend.service;

import com.parking.backend.dto.StaffRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Role;
import com.parking.backend.entity.User;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.RoleRepository;
import com.parking.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ParkingLotRepository parkingLotRepository;

    public StaffService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        ParkingLotRepository parkingLotRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public User registerStaff(StaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        if (userRepository.existsByDocument(request.getDocument())) {
            throw new RuntimeException("El documento ya está registrado");
        }

        Role staffRole = roleRepository.findByName("staff")
                .orElseThrow(() -> new RuntimeException("Rol 'staff' no encontrado en la base de datos"));

        ParkingLot parkingLot = parkingLotRepository.findById(request.getParkingLotId())
                .orElseThrow(() -> new RuntimeException("Parqueadero no encontrado con ID: " + request.getParkingLotId()));

        User user = new User();
        user.setDocument(request.getDocument());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setPasswordHash(AuthService.hashSha256(request.getPassword()));
        user.setRole(staffRole);
        user.setActive(true);
        user.setFailedAttempts(0);
        user.setBlocked(false);
        user.setParkingLot(parkingLot);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> getAllStaff(Long parkingLotId) {
        return userRepository.findByRole_NameAndParkingLot_Id("staff", parkingLotId);
    }

    public User unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        user.setFailedAttempts(0);
        user.setBlocked(false);

        return userRepository.save(user);
    }
}
