package com.parking.backend.service;

import com.parking.backend.dto.VehicleEntryRequest;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.entity.VehicleType;
import com.parking.backend.dto.VehicleExitRequest;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.ParkingLotRepository;
import com.parking.backend.repository.PaymentRepository;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.VehicleRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.Rate;
import com.parking.backend.entity.User;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryRecordServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private EntryRecordRepository entryRecordRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CellRepository cellRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @Mock
    private DiscountService discountService;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private EntryRecordService entryRecordService;

    private ParkingLot parkingLot;
    private VehicleType carType;
    private Vehicle car;
    private Vehicle bike;
    private Cell availableCell;
    private VehicleEntryRequest plateRequest;
    private VehicleEntryRequest bikeRequest;
    private User staffUser;
    private VehicleExitRequest exitRequest;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("ParKing Downtown");

        staffUser = new User();
        staffUser.setId(3L);
        staffUser.setUsername("lgomez");
        staffUser.setName("Laura Gomez");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "lgomez", null, List.of(new SimpleGrantedAuthority("ROLE_STAFF"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        carType = new VehicleType();
        carType.setId(1L);
        carType.setName("car");
        carType.setRequiresPlate(true);

        car = new Vehicle();
        car.setId(1L);
        car.setPlate("ABC-123");
        car.setVehicleType(carType);

        bike = new Vehicle();
        bike.setId(2L);
        bike.setBikeRegistration("BIKE-001");
        VehicleType bikeType = new VehicleType();
        bikeType.setId(2L);
        bikeType.setName("bicycle");
        bikeType.setRequiresPlate(false);
        bike.setVehicleType(bikeType);

        availableCell = new Cell();
        availableCell.setId(10L);
        availableCell.setParkingLot(parkingLot);
        availableCell.setVehicleType(carType);
        availableCell.setStatus("available");

        plateRequest = new VehicleEntryRequest();
        plateRequest.setParkingLotId(1L);
        plateRequest.setPlate("ABC-123");

        bikeRequest = new VehicleEntryRequest();
        bikeRequest.setParkingLotId(1L);
        bikeRequest.setBikeRegistration("BIKE-001");

        exitRequest = new VehicleExitRequest();
        exitRequest.setPlate("ABC-123");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Register entry with plate assigns cell and returns entry record")
    void registerEntryWithPlateSuccess() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatus(
                parkingLot, carType, "available"))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(plateRequest);

        assertNotNull(result);
        assertEquals(car, result.getVehicle());
        assertEquals(availableCell, result.getCell());
        assertEquals("active", result.getStatus());
        assertNotNull(result.getEntryTime());
        assertNotNull(result.getRecordedBy());
        assertEquals("Laura Gomez", result.getRecordedBy().getName());

        verify(cellRepository, times(1)).save(availableCell);
        assertEquals("occupied", availableCell.getStatus());
        verify(entryRecordRepository, times(1)).save(any(EntryRecord.class));
    }

    @Test
    @DisplayName("Register entry with bike registration assigns cell and returns entry record")
    void registerEntryWithBikeRegistrationSuccess() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByBikeRegistration("BIKE-001")).thenReturn(Optional.of(bike));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatus(
                parkingLot, bike.getVehicleType(), "available"))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(bikeRequest);

        assertNotNull(result);
        assertEquals(bike, result.getVehicle());
        assertEquals(availableCell, result.getCell());
        assertEquals("active", result.getStatus());
        assertNotNull(result.getEntryTime());

        verify(cellRepository, times(1)).save(availableCell);
        assertEquals("occupied", availableCell.getStatus());
        verify(entryRecordRepository, times(1)).save(any(EntryRecord.class));
    }

    @Test
    @DisplayName("Register entry with plate assigns recordedBy from authenticated user")
    void registerEntrySetsRecordedBy() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatus(
                parkingLot, carType, "available"))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(plateRequest);

        assertNotNull(result.getRecordedBy());
        assertEquals(3L, result.getRecordedBy().getId());
        assertEquals("lgomez", result.getRecordedBy().getUsername());
        verify(userRepository, times(1)).findByUsername("lgomez");
    }

    @Test
    @DisplayName("Register exit assigns recordedBy, creates payment, and returns exit response")
    void registerExitSetsRecordedBy() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(1L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusHours(2));
        activeRecord.setStatus("active");

        Rate rate = new Rate();
        rate.setId(1L);
        rate.setParkingLot(parkingLot);
        rate.setVehicleType(carType);
        rate.setRateType("flat");
        rate.setCost(new BigDecimal("50"));
        rate.setActive(true);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(Optional.of(rate));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = entryRecordService.registerExit(exitRequest);

        assertNotNull(response);
        assertEquals(1L, response.getEntryRecordId());
        assertEquals("ABC-123", response.getPlate());
        assertEquals("car", response.getVehicleType());
        assertNotNull(response.getExitTime());
        assertNotNull(response.getDuration());
        assertTrue(response.getDuration() > 0);
        assertEquals(new BigDecimal("50"), response.getSubtotal());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());
        assertEquals(new BigDecimal("50"), response.getTotalPaid());

        verify(entryRecordRepository, times(1)).save(argThat(r ->
                "completed".equals(r.getStatus())
                        && r.getRecordedBy() != null
                        && "lgomez".equals(r.getRecordedBy().getUsername())
        ));
        verify(userRepository, times(1)).findByUsername("lgomez");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Register entry when vehicle already inside throws exception")
    void registerEntryWithVehicleAlreadyInsideThrowsException() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(new EntryRecord()));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertEquals("Vehicle already inside parking lot", exception.getMessage());
        verify(cellRepository, never()).findFirstByParkingLotAndVehicleTypeAndStatus(any(), any(), any());
        verify(entryRecordRepository, never()).save(any());
    }
}
