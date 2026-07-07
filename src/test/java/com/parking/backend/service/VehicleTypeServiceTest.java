package com.parking.backend.service;

import com.parking.backend.dto.VehicleTypeResponse;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.repository.VehicleTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleTypeServiceTest {

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @InjectMocks
    private VehicleTypeService vehicleTypeService;

    @Test
    @DisplayName("getAll returns all vehicle types with correct fields")
    void getAllReturnsAllTypes() {
        VehicleType car = new VehicleType();
        car.setId(1L);
        car.setName("car");
        car.setRequiresPlate(true);

        VehicleType motorcycle = new VehicleType();
        motorcycle.setId(2L);
        motorcycle.setName("motorcycle");
        motorcycle.setRequiresPlate(true);

        VehicleType bicycle = new VehicleType();
        bicycle.setId(3L);
        bicycle.setName("bicycle");
        bicycle.setRequiresPlate(false);

        when(vehicleTypeRepository.findAll()).thenReturn(List.of(car, motorcycle, bicycle));

        List<VehicleTypeResponse> result = vehicleTypeService.getAll();

        assertEquals(3, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("car", result.get(0).getName());
        assertTrue(result.get(0).getRequiresPlate());

        assertEquals(2L, result.get(1).getId());
        assertEquals("motorcycle", result.get(1).getName());
        assertTrue(result.get(1).getRequiresPlate());

        assertEquals(3L, result.get(2).getId());
        assertEquals("bicycle", result.get(2).getName());
        assertFalse(result.get(2).getRequiresPlate());

        verify(vehicleTypeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll returns empty list when no vehicle types exist")
    void getAllReturnsEmptyWhenNoneExist() {
        when(vehicleTypeRepository.findAll()).thenReturn(List.of());

        List<VehicleTypeResponse> result = vehicleTypeService.getAll();

        assertTrue(result.isEmpty());
        verify(vehicleTypeRepository, times(1)).findAll();
    }
}
