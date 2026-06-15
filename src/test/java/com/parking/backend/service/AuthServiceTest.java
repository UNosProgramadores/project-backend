package com.parking.backend.service;

import com.parking.backend.dto.AuthResponse;
import com.parking.backend.dto.LoginRequest;
import com.parking.backend.dto.RegisterRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Role;
import com.parking.backend.entity.User;
import com.parking.backend.repository.RoleRepository;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User validUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("admin");

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("ParKing Downtown");

        validUser = new User();
        validUser.setId(1L);
        validUser.setUsername("cmendoza");
        validUser.setName("Carlos Mendoza");
        validUser.setPasswordHash(AuthService.hashSha256("admin123"));
        validUser.setRole(adminRole);
        validUser.setParkingLot(parkingLot);
        validUser.setActive(true);
        validUser.setBlocked(false);
        validUser.setFailedAttempts(0);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("cmendoza");
        loginRequest.setPassword("admin123");
    }

    @Test
    @DisplayName("Login with correct credentials returns a valid token")
    void loginWithCorrectCredentialsReturnsAuthResponse() {

        when(userRepository.findByUsername("cmendoza")).thenReturn(Optional.of(validUser));
        when(jwtUtil.generateToken(validUser)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals("mocked.jwt.token", response.getToken(), "Token should match the generated one");
        assertEquals("admin", response.getRole(), "Role should be admin");
        assertEquals(1L, response.getUserId(), "User ID should match");
        assertEquals("Carlos Mendoza", response.getName(), "Name should match");
        assertEquals(1L, response.getParkingLotId(), "Parking lot ID should match");

        verify(userRepository, times(1)).save(argThat(u -> u.getFailedAttempts() == 0));
    }

    @Test
    @DisplayName("Account is blocked after 5 consecutive failed login attempts")
    void loginAfter5FailedAttemptsBlocksAccount() {

        validUser.setFailedAttempts(4);

        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername("cmendoza");
        wrongRequest.setPassword("wrongpassword");  // incorrect password

        when(userRepository.findByUsername("cmendoza")).thenReturn(Optional.of(validUser));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(wrongRequest),
                "Should throw RuntimeException on failed login"
        );

        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository).save(argThat(u ->
                u.getFailedAttempts() == 5 && Boolean.TRUE.equals(u.getBlocked())
        ));
    }

    @Test
    @DisplayName("Register with an existing username throws an exception")
    void registerWithDuplicateUsernameThrowsException() {

        RegisterRequest request = new RegisterRequest();
        request.setDocument("99999999");
        request.setName("Juan Prueba");
        request.setPhone("3001234567");
        request.setUsername("cmendoza");   // already taken
        request.setPassword("test123");

        when(userRepository.existsByUsername("cmendoza")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(request),
                "Should throw RuntimeException when username is already taken"
        );

        assertEquals("Username already taken", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}