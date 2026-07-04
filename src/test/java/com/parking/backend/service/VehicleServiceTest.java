package com.parking.backend.service;

import com.parking.backend.dto.ClaimVehicleRequest;
import com.parking.backend.entity.User;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private User customer;
    private Vehicle unclaimedCar;
    private Vehicle claimedCar;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(10L);
        customer.setUsername("customer1");
        customer.setName("Pedro Cliente");

        User otherUser = new User();
        otherUser.setId(20L);
        otherUser.setUsername("otheruser");

        unclaimedCar = new Vehicle();
        unclaimedCar.setId(1L);
        unclaimedCar.setPlate("ABC-123");
        unclaimedCar.setOwner(null);

        claimedCar = new Vehicle();
        claimedCar.setId(2L);
        claimedCar.setPlate("XYZ-789");
        claimedCar.setOwner(otherUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "customer1", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Claim vehicle by plate successfully assigns owner")
    void claimVehicleByPlateSuccess() {
        ClaimVehicleRequest request = new ClaimVehicleRequest();
        request.setPlate("ABC-123");

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(unclaimedCar));
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehicle result = vehicleService.claimVehicle(request);

        assertNotNull(result);
        assertEquals(customer, result.getOwner());
        verify(vehicleRepository, times(1)).save(unclaimedCar);
    }

    @Test
    @DisplayName("Claim vehicle by bike registration successfully assigns owner")
    void claimVehicleByBikeSuccess() {
        Vehicle bike = new Vehicle();
        bike.setId(3L);
        bike.setBikeRegistration("BIKE-001");
        bike.setOwner(null);

        ClaimVehicleRequest request = new ClaimVehicleRequest();
        request.setBikeRegistration("BIKE-001");

        when(vehicleRepository.findByBikeRegistration("BIKE-001")).thenReturn(Optional.of(bike));
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehicle result = vehicleService.claimVehicle(request);

        assertNotNull(result);
        assertEquals(customer, result.getOwner());
        verify(vehicleRepository, times(1)).save(bike);
    }

    @Test
    @DisplayName("Claim vehicle that does not exist throws exception")
    void claimVehicleNotFound() {
        ClaimVehicleRequest request = new ClaimVehicleRequest();
        request.setPlate("NONEXISTENT");

        when(vehicleRepository.findByPlate("NONEXISTENT")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> vehicleService.claimVehicle(request));

        assertTrue(ex.getMessage().contains("not found"));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Claim vehicle already owned by another user throws exception")
    void claimVehicleAlreadyOwned() {
        ClaimVehicleRequest request = new ClaimVehicleRequest();
        request.setPlate("XYZ-789");

        when(vehicleRepository.findByPlate("XYZ-789")).thenReturn(Optional.of(claimedCar));
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> vehicleService.claimVehicle(request));

        assertTrue(ex.getMessage().contains("already associated"));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Claim vehicle without plate or bikeRegistration throws exception")
    void claimVehicleNoIdentifier() {
        ClaimVehicleRequest request = new ClaimVehicleRequest();
        request.setPlate(null);
        request.setBikeRegistration(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> vehicleService.claimVehicle(request));

        assertTrue(ex.getMessage().contains("required"));
    }
}
