package com.parking.backend.service;

import com.parking.backend.dto.AuthResponse;
import com.parking.backend.dto.LoginRequest;
import com.parking.backend.dto.RegisterRequest;
import com.parking.backend.entity.Role;
import com.parking.backend.entity.User;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.RoleRepository;
import com.parking.backend.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MIN_FAILED_ATTEMPTS = 0;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }


    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (Boolean.TRUE.equals(user.getBlocked())) {
            throw new RuntimeException("Account is blocked. Contact an administrator.");
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new RuntimeException("Account is inactive. Contact an administrator.");
        }

        String hashedPassword = hashSha256(request.getPassword());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            handleFailedAttempt(user);
            throw new RuntimeException("Invalid credentials");
        }

        user.setFailedAttempts(MIN_FAILED_ATTEMPTS);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        Long parkingLotId = user.getParkingLot() != null ? user.getParkingLot().getId() : null;

        return new AuthResponse(token, user.getRole().getName(), user.getId(), user.getName(), parkingLotId);
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.existsByDocument(request.getDocument())) {
            throw new RuntimeException("Document already registered");
        }

        Role customerRole = roleRepository.findByName("customer")
                .orElseThrow(() -> new RuntimeException("Role 'customer' not found in database"));


        User user = new User();
        user.setDocument(request.getDocument());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setPasswordHash(hashSha256(request.getPassword()));
        user.setRole(customerRole);
        user.setActive(true);
        user.setFailedAttempts(MIN_FAILED_ATTEMPTS);
        user.setBlocked(false);
        user.setParkingLot(null);  // customers have no assigned parking lot
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, customerRole.getName(), user.getId(), user.getName(), null);
    }

    private void handleFailedAttempt(User user) {
        int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
        attempts++;
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setBlocked(true);
        }

        userRepository.save(user);
    }

    public static String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}