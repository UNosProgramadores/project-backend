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
import com.parking.backend.entity.Invoice;
import com.parking.backend.repository.InvoiceRepository;
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
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private InvoiceRepository invoiceRepository;

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
        parkingLot.setAutoAssignment(true);

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
        exitRequest.setParkingLotId(1L);
        exitRequest.setPaymentMethod("cash");
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
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false))
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
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, bike.getVehicleType(), "available", false))
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
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false))
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
    @DisplayName("Register exit with paymentMethod creates Payment with method set")
    void registerExitWithPaymentMethod() {
        parkingLot.setAutoAssignment(true);

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

        exitRequest.setPaymentMethod("CASH");

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(rate));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

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

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        assertEquals("CASH", paymentCaptor.getValue().getPaymentMethod());

        verify(entryRecordRepository, times(1)).save(argThat(r ->
                "completed".equals(r.getStatus())
                        && r.getRecordedBy() != null
                        && "lgomez".equals(r.getRecordedBy().getUsername())
        ));
        verify(userRepository, times(1)).findByUsername("lgomez");
    }

    @Test
    @DisplayName("Register exit without paymentMethod saves Payment with null method")
    void registerExitWithoutPaymentMethod() {
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

        exitRequest.setPaymentMethod(null);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(rate));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertNotNull(response);
        assertEquals(new BigDecimal("50"), response.getTotalPaid());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        assertNull(paymentCaptor.getValue().getPaymentMethod());
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

        assertEquals("El vehículo ya se encuentra dentro del parqueadero", exception.getMessage());
        verify(cellRepository, never()).findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(any(), any(), any(), any());
        verify(entryRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Manual cell assignment succeeds when autoAssignment is disabled and cellId is valid")
    void manualCellAssignmentSuccess() {
        parkingLot.setAutoAssignment(false);
        availableCell.setCellType("parking");
        availableCell.setVehicleType(carType);

        plateRequest.setCellId(10L);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findByIdAndParkingLotAndActiveTrue(10L, parkingLot)).thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(plateRequest);

        assertNotNull(result);
        assertEquals(car, result.getVehicle());
        assertEquals(availableCell, result.getCell());
        assertEquals("active", result.getStatus());

        verify(cellRepository, times(1)).save(availableCell);
        assertEquals("occupied", availableCell.getStatus());
        verify(cellRepository, never()).findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Manual cell assignment throws when autoAssignment disabled and no cellId provided")
    void manualCellAssignmentMissingCellId() {
        parkingLot.setAutoAssignment(false);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertEquals("La asignación automática está deshabilitada. Se requiere un cellId.", exception.getMessage());
        verify(cellRepository, never()).findByIdAndParkingLotAndActiveTrue(any(), any());
    }

    @Test
    @DisplayName("Manual cell assignment throws when cell not found in parking lot")
    void manualCellAssignmentCellNotFound() {
        parkingLot.setAutoAssignment(false);
        plateRequest.setCellId(99L);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.empty());
        when(cellRepository.findByIdAndParkingLotAndActiveTrue(99L, parkingLot)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertEquals("Celda no encontrada en este parqueadero", exception.getMessage());
    }

    @Test
    @DisplayName("Manual cell assignment throws when cell is occupied")
    void manualCellAssignmentOccupied() {
        parkingLot.setAutoAssignment(false);
        availableCell.setCellType("parking");
        availableCell.setVehicleType(carType);
        availableCell.setStatus("occupied");
        plateRequest.setCellId(10L);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.empty());
        when(cellRepository.findByIdAndParkingLotAndActiveTrue(10L, parkingLot)).thenReturn(Optional.of(availableCell));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertEquals("La celda seleccionada no está disponible", exception.getMessage());
    }

    @Test
    @DisplayName("Manual cell assignment throws when cell is transit type")
    void manualCellAssignmentTransitCell() {
        parkingLot.setAutoAssignment(false);
        availableCell.setCellType("transit");
        availableCell.setStatus("available");
        plateRequest.setCellId(10L);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.empty());
        when(cellRepository.findByIdAndParkingLotAndActiveTrue(10L, parkingLot)).thenReturn(Optional.of(availableCell));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertEquals("La celda seleccionada no es de tipo estacionamiento", exception.getMessage());
    }

    @Test
    @DisplayName("Manual cell assignment throws when cell does not support vehicle type")
    void manualCellAssignmentWrongVehicleType() {
        parkingLot.setAutoAssignment(false);
        availableCell.setCellType("parking");
        availableCell.setStatus("available");
        VehicleType motorcycleType = new VehicleType();
        motorcycleType.setId(3L);
        motorcycleType.setName("motorcycle");
        availableCell.setVehicleType(motorcycleType);
        plateRequest.setCellId(10L);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.empty());
        when(cellRepository.findByIdAndParkingLotAndActiveTrue(10L, parkingLot)).thenReturn(Optional.of(availableCell));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest)
        );

        assertTrue(exception.getMessage().contains("no soporta el tipo de vehículo"));
    }

    @Test
    @DisplayName("Auto-assignment excludes reserved for staff cells")
    void autoAssignmentExcludesStaffCells() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(plateRequest);

        assertNotNull(result);
        assertEquals(availableCell, result.getCell());

        verify(cellRepository).findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false);
    }

    @Test
    @DisplayName("Bug fix: Auto-assignment ignores inactive cells (active=false from shrink)")
    void autoAssignmentExcludesInactiveCells() {
        Cell inactiveCell = new Cell();
        inactiveCell.setId(99L);
        inactiveCell.setParkingLot(parkingLot);
        inactiveCell.setRow(-1);
        inactiveCell.setCol(-1);
        inactiveCell.setActive(false);
        inactiveCell.setVehicleType(carType);
        inactiveCell.setStatus("available");
        availableCell.setActive(true);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntryRecord result = entryRecordService.registerEntry(plateRequest);

        assertNotNull(result);
        assertEquals(availableCell, result.getCell());
        assertEquals(10L, result.getCell().getId(),
                "Must pick the active cell (10L), not the inactive one (99L)");
        verify(cellRepository).findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false);
    }

    @Test
    @DisplayName("Register exit fails when parkingLotId does not match the vehicle's actual parking lot")
    void registerExitWithWrongParkingLotThrowsException() {
        ParkingLot otherLot = new ParkingLot();
        otherLot.setId(99L);
        otherLot.setName("Other Lot");

        Cell cellInOtherLot = new Cell();
        cellInOtherLot.setId(99L);
        cellInOtherLot.setParkingLot(otherLot);
        cellInOtherLot.setVehicleType(carType);
        cellInOtherLot.setStatus("available");

        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(1L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(cellInOtherLot);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusHours(1));
        activeRecord.setStatus("active");

        exitRequest.setParkingLotId(1L);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> entryRecordService.registerExit(exitRequest)
        );

        assertEquals("El vehiculo no se encuentra en este parqueadero", exception.getMessage());
        verify(paymentRepository, never()).save(any());
        verify(cellRepository, never()).save(any());
        verify(entryRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register exit succeeds when parkingLotId matches the vehicle's actual parking lot")
    void registerExitWithCorrectParkingLotSucceeds() {
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

        exitRequest.setParkingLotId(1L);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(rate));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(200L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertNotNull(response);
        assertEquals("ABC-123", response.getPlate());
        assertEquals(new BigDecimal("50"), response.getSubtotal());
        assertEquals("cash", response.getPaymentMethod());
        assertEquals(200L, response.getInvoiceId());

        verify(entryRecordRepository, times(1)).save(argThat(r ->
                "completed".equals(r.getStatus())
        ));
        verify(paymentRepository, times(1)).save(argThat(p ->
                "cash".equals(p.getPaymentMethod())
        ));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Register exit for bike generates invoice with bikeRegistration")
    void registerExitForBikeGeneratesInvoice() {
        Cell bikeCell = new Cell();
        bikeCell.setId(20L);
        bikeCell.setParkingLot(parkingLot);
        bikeCell.setVehicleType(bike.getVehicleType());
        bikeCell.setStatus("available");

        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(2L);
        activeRecord.setVehicle(bike);
        activeRecord.setCell(bikeCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusHours(1));
        activeRecord.setStatus("active");

        Rate rate = new Rate();
        rate.setId(2L);
        rate.setParkingLot(parkingLot);
        rate.setVehicleType(bike.getVehicleType());
        rate.setRateType("flat");
        rate.setCost(new BigDecimal("10"));
        rate.setActive(true);

        VehicleExitRequest bikeExit = new VehicleExitRequest();
        bikeExit.setBikeRegistration("BIKE-001");
        bikeExit.setParkingLotId(1L);
        bikeExit.setPaymentMethod("card");

        when(vehicleRepository.findByBikeRegistration("BIKE-001")).thenReturn(Optional.of(bike));
        when(entryRecordRepository.findByVehicleAndStatus(bike, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, bike.getVehicleType(), true))
                .thenReturn(List.of(rate));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(200L);
            return p;
        });
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(300L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(bikeExit);

        assertNotNull(response);
        assertEquals("BIKE-001", response.getBikeRegistration());
        assertEquals("bicycle", response.getVehicleType());
        assertEquals("card", response.getPaymentMethod());
        assertEquals(300L, response.getInvoiceId());
        assertEquals(new BigDecimal("10"), response.getSubtotal());

        verify(paymentRepository, times(1)).save(argThat(p ->
                "card".equals(p.getPaymentMethod())
        ));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("registerEntry rolls back when entryRecordRepository.save fails (transactional)")
    void registerEntryTransactionalRollbackOnSaveFailure() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(cellRepository.findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaffAndActiveTrue(
                parkingLot, carType, "available", false))
                .thenReturn(Optional.of(availableCell));
        when(entryRecordRepository.save(any(EntryRecord.class)))
                .thenThrow(new RuntimeException("DB failure"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> entryRecordService.registerEntry(plateRequest));

        assertTrue(ex.getMessage().contains("DB failure"));
        verify(cellRepository, times(1)).save(availableCell);
        verify(entryRecordRepository, times(1)).save(any(EntryRecord.class));
    }

    @Test
    @DisplayName("H-07: Register exit with vehicle owned by customer applies discount correctly")
    void registerExitWithVehicleOwnerAppliesDiscount() {
        User customerOwner = new User();
        customerOwner.setId(50L);
        customerOwner.setUsername("customer1");

        Vehicle ownedCar = new Vehicle();
        ownedCar.setId(100L);
        ownedCar.setPlate("OWNED-01");
        ownedCar.setVehicleType(carType);
        ownedCar.setOwner(customerOwner);

        Cell ownedCell = new Cell();
        ownedCell.setId(30L);
        ownedCell.setParkingLot(parkingLot);
        ownedCell.setVehicleType(carType);
        ownedCell.setStatus("occupied");

        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(50L);
        activeRecord.setVehicle(ownedCar);
        activeRecord.setCell(ownedCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusHours(3));
        activeRecord.setStatus("active");

        Rate rate = new Rate();
        rate.setId(5L);
        rate.setParkingLot(parkingLot);
        rate.setVehicleType(carType);
        rate.setRateType("per_minute");
        rate.setCost(new BigDecimal("2"));
        rate.setActive(true);

        VehicleExitRequest exitReq = new VehicleExitRequest();
        exitReq.setPlate("OWNED-01");
        exitReq.setParkingLotId(1L);
        exitReq.setPaymentMethod("cash");

        when(vehicleRepository.findByPlate("OWNED-01")).thenReturn(Optional.of(ownedCar));
        when(entryRecordRepository.findByVehicleAndStatus(ownedCar, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(rate));
        when(discountService.calculateDiscount(eq(parkingLot), eq(customerOwner), any()))
                .thenReturn(new BigDecimal("36"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(500L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitReq);

        assertNotNull(response);
        assertEquals("OWNED-01", response.getPlate());
        assertEquals(180, response.getDuration());
        assertEquals(new BigDecimal("360"), response.getSubtotal());
        assertEquals(new BigDecimal("36"), response.getDiscountAmount());
        assertEquals(new BigDecimal("324"), response.getTotalPaid());
        assertEquals(new BigDecimal("10"), response.getDiscountPercentage());
        assertEquals(500L, response.getInvoiceId());

        verify(discountService, times(1)).calculateDiscount(eq(parkingLot), eq(customerOwner), any());
    }

    // ──────────────────────────────────────────────
    // Flat-rate threshold tests (a-f)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("(a) duration < 360 min with both rates → per_minute * duration")
    void exitDurationLessThan360UsesPerMinute() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(10L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusMinutes(120));
        activeRecord.setStatus("active");

        Rate perMinute = new Rate();
        perMinute.setId(1L);
        perMinute.setParkingLot(parkingLot);
        perMinute.setVehicleType(carType);
        perMinute.setRateType("per_minute");
        perMinute.setCost(new BigDecimal("2"));
        perMinute.setActive(true);

        Rate flat = new Rate();
        flat.setId(2L);
        flat.setParkingLot(parkingLot);
        flat.setVehicleType(carType);
        flat.setRateType("flat");
        flat.setCost(new BigDecimal("360"));
        flat.setActive(true);

        // flat cost 360 vs 120 min * 2 = 240; flat would be more expensive,
        // but the rule says < 360 → per_minute regardless
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(perMinute, flat));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertEquals(120, response.getDuration());
        assertEquals(new BigDecimal("240"), response.getSubtotal());
    }

    @Test
    @DisplayName("(b) duration >= 360 min with both rates → flat cost")
    void exitDurationAtLeast360UsesFlat() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(11L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusMinutes(400));
        activeRecord.setStatus("active");

        Rate perMinute = new Rate();
        perMinute.setId(1L);
        perMinute.setParkingLot(parkingLot);
        perMinute.setVehicleType(carType);
        perMinute.setRateType("per_minute");
        perMinute.setCost(new BigDecimal("2"));
        perMinute.setActive(true);

        Rate flat = new Rate();
        flat.setId(2L);
        flat.setParkingLot(parkingLot);
        flat.setVehicleType(carType);
        flat.setRateType("flat");
        flat.setCost(new BigDecimal("360"));
        flat.setActive(true);

        // 400 min * 2 = 800 if per_minute, but flat = 360 is cheaper → business rule applies
        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(perMinute, flat));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertEquals(400, response.getDuration());
        assertEquals(new BigDecimal("360"), response.getSubtotal());
    }

    @Test
    @DisplayName("(c) duration == 360 min exactly → flat cost (>= threshold)")
    void exitDurationExactly360UsesFlat() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(12L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusMinutes(360));
        activeRecord.setStatus("active");

        Rate perMinute = new Rate();
        perMinute.setId(1L);
        perMinute.setParkingLot(parkingLot);
        perMinute.setVehicleType(carType);
        perMinute.setRateType("per_minute");
        perMinute.setCost(new BigDecimal("2"));
        perMinute.setActive(true);

        Rate flat = new Rate();
        flat.setId(2L);
        flat.setParkingLot(parkingLot);
        flat.setVehicleType(carType);
        flat.setRateType("flat");
        flat.setCost(new BigDecimal("360"));
        flat.setActive(true);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(perMinute, flat));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertEquals(360, response.getDuration());
        assertEquals(new BigDecimal("360"), response.getSubtotal(),
                "Exactly 360 min should apply flat (>=)");
    }

    @Test
    @DisplayName("(d) only per_minute rate, long duration → per_minute * duration (no flat fallback)")
    void exitOnlyPerMinuteLongDurationUsesPerMinute() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(13L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusMinutes(500));
        activeRecord.setStatus("active");

        Rate perMinute = new Rate();
        perMinute.setId(1L);
        perMinute.setParkingLot(parkingLot);
        perMinute.setVehicleType(carType);
        perMinute.setRateType("per_minute");
        perMinute.setCost(new BigDecimal("2"));
        perMinute.setActive(true);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(perMinute));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        var response = entryRecordService.registerExit(exitRequest);

        assertEquals(500, response.getDuration());
        assertEquals(new BigDecimal("1000"), response.getSubtotal());
    }

    @Test
    @DisplayName("(f) original bug scenario: two active rates no longer throws unique result")
    void exitWithBothActiveRatesDoesNotThrow() {
        EntryRecord activeRecord = new EntryRecord();
        activeRecord.setId(14L);
        activeRecord.setVehicle(car);
        activeRecord.setCell(availableCell);
        activeRecord.setEntryTime(java.time.LocalDateTime.now().minusMinutes(180));
        activeRecord.setStatus("active");

        Rate perMinute = new Rate();
        perMinute.setId(1L);
        perMinute.setParkingLot(parkingLot);
        perMinute.setVehicleType(carType);
        perMinute.setRateType("per_minute");
        perMinute.setCost(new BigDecimal("2"));
        perMinute.setActive(true);

        Rate flat = new Rate();
        flat.setId(2L);
        flat.setParkingLot(parkingLot);
        flat.setVehicleType(carType);
        flat.setRateType("flat");
        flat.setCost(new BigDecimal("360"));
        flat.setActive(true);

        when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(car));
        when(entryRecordRepository.findByVehicleAndStatus(car, "active"))
                .thenReturn(Optional.of(activeRecord));
        when(userRepository.findByUsername("lgomez")).thenReturn(Optional.of(staffUser));
        when(rateRepository.findByParkingLotAndVehicleTypeAndActive(parkingLot, carType, true))
                .thenReturn(List.of(perMinute, flat));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
            Invoice invEntity = inv.getArgument(0);
            invEntity.setId(999L);
            return invEntity;
        });

        assertDoesNotThrow(() -> {
            var response = entryRecordService.registerExit(exitRequest);
            assertNotNull(response);
            assertEquals(180, response.getDuration());
            assertEquals(new BigDecimal("360"), response.getSubtotal());
        });
    }
}
