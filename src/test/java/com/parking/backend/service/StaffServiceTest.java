package com.parking.backend.service;

import com.parking.backend.dto.StaffRequest;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Role;
import com.parking.backend.entity.User;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.RoleRepository;
import com.parking.backend.repository.UserRepository;
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
class StaffServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private StaffService staffService;

    @Test
    @DisplayName("Register staff successfully creates a staff user with parking lot")
    void registerStaffSuccess() {
        StaffRequest request = new StaffRequest();
        request.setDocument("12345678");
        request.setName("Juan Staff");
        request.setPhone("3001234567");
        request.setUsername("jstaff");
        request.setPassword("staff123");
        request.setParkingLotId(1L);

        Role staffRole = new Role();
        staffRole.setId(2L);
        staffRole.setName("staff");

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("ParKing Downtown");

        when(userRepository.existsByUsername("jstaff")).thenReturn(false);
        when(userRepository.existsByDocument("12345678")).thenReturn(false);
        when(roleRepository.findByName("staff")).thenReturn(Optional.of(staffRole));
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        User result = staffService.registerStaff(request);

        assertNotNull(result);
        assertEquals("jstaff", result.getUsername());
        assertEquals("Juan Staff", result.getName());
        assertEquals("12345678", result.getDocument());
        assertEquals(staffRole, result.getRole());
        assertEquals(parkingLot, result.getParkingLot());
        assertTrue(result.getActive());
        assertFalse(result.getBlocked());
        assertEquals(0, result.getFailedAttempts());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register staff with duplicate username throws exception")
    void registerStaffDuplicateUsername() {
        StaffRequest request = new StaffRequest();
        request.setDocument("12345678");
        request.setName("Juan Staff");
        request.setUsername("existing");
        request.setPassword("staff123");
        request.setParkingLotId(1L);

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> staffService.registerStaff(request));

        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register staff with duplicate document throws exception")
    void registerStaffDuplicateDocument() {
        StaffRequest request = new StaffRequest();
        request.setDocument("99999999");
        request.setName("Juan Staff");
        request.setUsername("jstaff");
        request.setPassword("staff123");
        request.setParkingLotId(1L);

        when(userRepository.existsByUsername("jstaff")).thenReturn(false);
        when(userRepository.existsByDocument("99999999")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> staffService.registerStaff(request));

        assertEquals("El documento ya está registrado", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register staff with invalid parking lot throws exception")
    void registerStaffInvalidParkingLot() {
        StaffRequest request = new StaffRequest();
        request.setDocument("12345678");
        request.setName("Juan Staff");
        request.setUsername("jstaff");
        request.setPassword("staff123");
        request.setParkingLotId(99L);

        Role staffRole = new Role();
        staffRole.setId(2L);
        staffRole.setName("staff");

        when(userRepository.existsByUsername("jstaff")).thenReturn(false);
        when(userRepository.existsByDocument("12345678")).thenReturn(false);
        when(roleRepository.findByName("staff")).thenReturn(Optional.of(staffRole));
        when(parkingLotRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> staffService.registerStaff(request));

        assertTrue(ex.getMessage().contains("99"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Unlock user resets failedAttempts and blocked flag")
    void unlockUserSuccess() {
        User blockedUser = new User();
        blockedUser.setId(5L);
        blockedUser.setUsername("blockeduser");
        blockedUser.setFailedAttempts(5);
        blockedUser.setBlocked(true);

        when(userRepository.findById(5L)).thenReturn(Optional.of(blockedUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = staffService.unlockUser(5L);

        assertEquals(0, result.getFailedAttempts());
        assertFalse(result.getBlocked());
        verify(userRepository, times(1)).save(blockedUser);
    }

    @Test
    @DisplayName("Unlock non-existent user throws exception")
    void unlockUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> staffService.unlockUser(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(userRepository, never()).save(any());
    }
}
