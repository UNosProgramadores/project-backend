package com.parking.backend.service;

import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.PaymentRepository;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.VehicleRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import com.parking.backend.repository.ParkingLotRepository;

import com.parking.backend.dto.VehicleEntryRequest;
import com.parking.backend.dto.VehicleExitRequest;
import com.parking.backend.dto.VehicleExitResponse;
import com.parking.backend.entity.Cell;
import com.parking.backend.entity.EntryRecord;
import com.parking.backend.entity.ParkingLot;
import com.parking.backend.entity.Payment;
import com.parking.backend.entity.Rate;
import com.parking.backend.entity.User;
import com.parking.backend.entity.Vehicle;
import com.parking.backend.entity.VehicleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class EntryRecordService {

    private final ParkingLotRepository parkingLotRepository;
    private final EntryRecordRepository entryRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final CellRepository cellRepository;
    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final DiscountService discountService;
    private final PaymentRepository paymentRepository;

    public EntryRecordService(ParkingLotRepository parkingLotRepository,
                              EntryRecordRepository entryRecordRepository,
                              VehicleRepository vehicleRepository,
                              CellRepository cellRepository,
                              RateRepository rateRepository,
                              UserRepository userRepository,
                              VehicleTypeRepository vehicleTypeRepository,
                              DiscountService discountService,
                              PaymentRepository paymentRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.entryRecordRepository = entryRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.cellRepository = cellRepository;
        this.rateRepository = rateRepository;
        this.userRepository = userRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.discountService = discountService;
        this.paymentRepository = paymentRepository;
    }

    private Vehicle findOrCreateVehicle(String plate, String bikeRegistration, Long vehicleTypeId) {
        if (plate != null && !plate.isBlank()) {
            return vehicleRepository.findByPlate(plate)
                    .orElseGet(() -> {
                        Vehicle v = new Vehicle();
                        v.setPlate(plate);
                        v.setVehicleType(vehicleTypeRepository.findById(vehicleTypeId)
                                .orElseThrow(() -> new RuntimeException("Invalid vehicle type")));
                        v.setActive(true);
                        return vehicleRepository.save(v);
                    });
        }
        if (bikeRegistration != null && !bikeRegistration.isBlank()) {
            return vehicleRepository.findByBikeRegistration(bikeRegistration)
                    .orElseGet(() -> {
                        Vehicle v = new Vehicle();
                        v.setBikeRegistration(bikeRegistration);
                        v.setVehicleType(vehicleTypeRepository.findById(vehicleTypeId)
                                .orElseThrow(() -> new RuntimeException("Invalid vehicle type")));
                        v.setActive(true);
                        return vehicleRepository.save(v);
                    });
        }
        throw new RuntimeException("Plate or bike registration is required");
    }

    public EntryRecord registerEntry(VehicleEntryRequest request) {

        ParkingLot parkingLot = parkingLotRepository.findById(
                request.getParkingLotId()
        ).orElseThrow(() ->
                new RuntimeException("Parking lot not found")
        );

        Vehicle vehicle = findOrCreateVehicle(
                request.getPlate(), request.getBikeRegistration(), request.getVehicleTypeId()
        );
        entryRecordRepository.findByVehicleAndStatus(
                vehicle,
                "active"
        ).ifPresent(record -> {
            throw new RuntimeException(
                    "Vehicle already inside parking lot"
            );
        });

        Cell cell = resolveCell(parkingLot, vehicle, request);

        EntryRecord entryRecord = new EntryRecord();

        entryRecord.setVehicle(vehicle);
        entryRecord.setCell(cell);
        entryRecord.setRecordedBy(getCurrentUser());
        entryRecord.setEntryTime(LocalDateTime.now());
        entryRecord.setStatus("active");

        cell.setStatus("occupied");

        cellRepository.save(cell);

        return entryRecordRepository.save(entryRecord);
    }

    private Cell resolveCell(ParkingLot parkingLot, Vehicle vehicle, VehicleEntryRequest request) {
        if (Boolean.FALSE.equals(parkingLot.getAutoAssignment())) {
            if (request.getCellId() == null) {
                throw new RuntimeException(
                        "Auto-assignment is disabled. A cellId is required."
                );
            }
            Cell cell = cellRepository.findByIdAndParkingLot(request.getCellId(), parkingLot)
                    .orElseThrow(() -> new RuntimeException(
                            "Cell not found in this parking lot"
                    ));
            if (!"available".equals(cell.getStatus())) {
                throw new RuntimeException("Selected cell is not available");
            }
            if (!"parking".equals(cell.getCellType())) {
                throw new RuntimeException("Selected cell is not a parking cell");
            }
            if (!vehicle.getVehicleType().getId().equals(cell.getVehicleType().getId())) {
                throw new RuntimeException(
                        "Selected cell does not support vehicle type " + vehicle.getVehicleType().getName()
                );
            }
            return cell;
        }
        return cellRepository
                .findFirstByParkingLotAndVehicleTypeAndStatusAndReservedForStaff(
                        parkingLot,
                        vehicle.getVehicleType(),
                        "available",
                        false
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No available cell found"
                        )
                );
    }

    public VehicleExitResponse registerExit(VehicleExitRequest request) {

        Vehicle vehicle;

        if (request.getPlate() != null && !request.getPlate().isBlank()) {
            vehicle = vehicleRepository.findByPlate(request.getPlate())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        } else if (request.getBikeRegistration() != null && !request.getBikeRegistration().isBlank()) {
            vehicle = vehicleRepository.findByBikeRegistration(request.getBikeRegistration())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        } else {
            throw new RuntimeException("Plate or bike registration is required");
        }

        EntryRecord record = entryRecordRepository.findByVehicleAndStatus(vehicle, "active")
                .orElseThrow(() -> new RuntimeException("No active entry found for this vehicle"));

        LocalDateTime exitTime = LocalDateTime.now();
        int duration = (int) ChronoUnit.MINUTES.between(record.getEntryTime(), exitTime);

        record.setExitTime(exitTime);
        record.setDuration(duration);
        record.setStatus("completed");
        record.setRecordedBy(getCurrentUser());

        Cell cell = record.getCell();
        cell.setStatus("available");
        cellRepository.save(cell);

        entryRecordRepository.save(record);

        ParkingLot parkingLot = cell.getParkingLot();
        Rate rate = rateRepository
                .findByParkingLotAndVehicleTypeAndActive(parkingLot, vehicle.getVehicleType(), true)
                .orElseThrow(() -> new RuntimeException("No active rate found for this vehicle type"));

        BigDecimal subtotal = "per_minute".equals(rate.getRateType())
                ? rate.getCost().multiply(BigDecimal.valueOf(duration))
                : rate.getCost();

        User owner = vehicle.getOwner();
        BigDecimal discountAmount = (owner != null)
                ? discountService.calculateDiscount(parkingLot, owner, subtotal)
                : BigDecimal.ZERO;
        BigDecimal totalPaid = subtotal.subtract(discountAmount);

        BigDecimal discountPercentage = discountAmount.compareTo(BigDecimal.ZERO) > 0
                ? discountAmount.multiply(BigDecimal.valueOf(100)).divide(subtotal, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Payment payment = new Payment();
        payment.setEntryRecord(record);
        payment.setSubtotal(subtotal);
        payment.setDiscountPercentage(discountPercentage);
        payment.setDiscountAmount(discountAmount);
        payment.setTotalPaid(totalPaid);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        VehicleExitResponse response = new VehicleExitResponse();
        response.setEntryRecordId(record.getId());
        response.setPlate(vehicle.getPlate());
        response.setBikeRegistration(vehicle.getBikeRegistration());
        response.setVehicleType(vehicle.getVehicleType().getName());
        response.setCellCode(cell.getCode());
        response.setEntryTime(record.getEntryTime());
        response.setExitTime(exitTime);
        response.setDuration(duration);
        response.setSubtotal(subtotal);
        response.setDiscountPercentage(discountPercentage);
        response.setDiscountAmount(discountAmount);
        response.setTotalPaid(totalPaid);

        return response;
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