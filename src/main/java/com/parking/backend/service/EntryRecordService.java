package com.parking.backend.service;

import com.parking.backend.entity.Invoice;
import com.parking.backend.repository.CellRepository;
import com.parking.backend.repository.EntryRecordRepository;
import com.parking.backend.repository.InvoiceRepository;
import com.parking.backend.repository.PaymentRepository;
import com.parking.backend.repository.RateRepository;
import com.parking.backend.repository.UserRepository;
import com.parking.backend.repository.VehicleRepository;
import com.parking.backend.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.parking.backend.repository.ParkingLotRepository;

import com.parking.backend.dto.ActiveEntryResponse;
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
import java.util.List;
import java.util.UUID;

@Service
public class EntryRecordService {

    private static final int FLAT_RATE_THRESHOLD_MINUTES = 360;
    // flat-rate threshold: stays >= 6 hours (360 min) charge the flat amount instead of per-minute

    private final ParkingLotRepository parkingLotRepository;
    private final EntryRecordRepository entryRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final CellRepository cellRepository;
    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final DiscountService discountService;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public EntryRecordService(ParkingLotRepository parkingLotRepository,
                              EntryRecordRepository entryRecordRepository,
                              VehicleRepository vehicleRepository,
                              CellRepository cellRepository,
                              RateRepository rateRepository,
                              UserRepository userRepository,
                              VehicleTypeRepository vehicleTypeRepository,
                              DiscountService discountService,
                              PaymentRepository paymentRepository,
                              InvoiceRepository invoiceRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.entryRecordRepository = entryRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.cellRepository = cellRepository;
        this.rateRepository = rateRepository;
        this.userRepository = userRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.discountService = discountService;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    private Vehicle findOrCreateVehicle(String plate, String bikeRegistration, Long vehicleTypeId, String ownerDocument) {
        Vehicle vehicle;
        if (plate != null && !plate.isBlank()) {
            vehicle = vehicleRepository.findByPlate(plate).orElseGet(() -> {
                if (vehicleTypeId == null) {
                    throw new RuntimeException("vehicleTypeId es requerido");
                }
                VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                        .orElseThrow(() -> new RuntimeException("Tipo de vehículo inválido"));
                boolean requiresPlate = Boolean.TRUE.equals(vehicleType.getRequiresPlate());
                if (!requiresPlate) {
                    throw new RuntimeException("El tipo de vehículo proporcionado no acepta placa");
                }
                Vehicle v = new Vehicle();
                v.setPlate(plate);
                v.setVehicleType(vehicleType);
                v.setActive(true);
                return vehicleRepository.save(v);
            });
        } else if (bikeRegistration != null && !bikeRegistration.isBlank()) {
            vehicle = vehicleRepository.findByBikeRegistration(bikeRegistration).orElseGet(() -> {
                if (vehicleTypeId == null) {
                    throw new RuntimeException("vehicleTypeId es requerido");
                }
                VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                        .orElseThrow(() -> new RuntimeException("Tipo de vehículo inválido"));
                boolean requiresPlate = Boolean.TRUE.equals(vehicleType.getRequiresPlate());
                if (requiresPlate) {
                    throw new RuntimeException("El tipo de vehículo proporcionado requiere placa, no bikeRegistration");
                }
                Vehicle v = new Vehicle();
                v.setBikeRegistration(bikeRegistration);
                v.setVehicleType(vehicleType);
                v.setActive(true);
                return vehicleRepository.save(v);
            });
        } else {
            throw new RuntimeException("Placa o registro de bicicleta es requerido");
        }

        assignOwnerIfPresent(vehicle, ownerDocument);
        return vehicle;
    }

    private void assignOwnerIfPresent(Vehicle vehicle, String ownerDocument) {
        if (ownerDocument != null && !ownerDocument.isBlank()) {
            User owner = userRepository.findByDocumentAndRole_Name(ownerDocument, "customer")
                    .orElseThrow(() -> new RuntimeException("No se encontró un cliente con documento: " + ownerDocument));
            vehicle.setOwner(owner);
            vehicleRepository.save(vehicle);
        }
    }

    @Transactional
    public EntryRecord registerEntry(VehicleEntryRequest request) {

        ParkingLot parkingLot = parkingLotRepository.findById(
                request.getParkingLotId()
        ).orElseThrow(() ->
                new RuntimeException("Parqueadero no encontrado")
        );

        Vehicle vehicle = findOrCreateVehicle(
                request.getPlate(), request.getBikeRegistration(), request.getVehicleTypeId(), request.getOwnerDocument()
        );
        entryRecordRepository.findByVehicleAndStatus(
                vehicle,
                "active"
        ).ifPresent(record -> {
            throw new RuntimeException(
                    "El vehículo ya se encuentra dentro del parqueadero"
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
                                "La asignación automática está deshabilitada. Se requiere un cellId."
                        );
            }
            Cell cell = cellRepository.findByIdAndParkingLot(request.getCellId(), parkingLot)
                    .orElseThrow(() -> new RuntimeException(
                            "Celda no encontrada en este parqueadero"
                    ));
            if (!"available".equals(cell.getStatus())) {
                throw new RuntimeException("La celda seleccionada no está disponible");
            }
            if (!"parking".equals(cell.getCellType())) {
                throw new RuntimeException("La celda seleccionada no es de tipo estacionamiento");
            }
            if (!vehicle.getVehicleType().getId().equals(cell.getVehicleType().getId())) {
                throw new RuntimeException(
                        "La celda seleccionada no soporta el tipo de vehículo " + vehicle.getVehicleType().getName()
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
                                "No se encontró una celda disponible"
                        )
                );
    }

    @Transactional
    public VehicleExitResponse registerExit(VehicleExitRequest request) {

        Vehicle vehicle = null;
        String[] values = { request.getPlate(), request.getBikeRegistration() };

        for (String v : values) {
            if (v == null || v.isBlank()) continue;
            vehicle = vehicleRepository.findByPlate(v).orElse(null);
            if (vehicle != null) break;
            vehicle = vehicleRepository.findByBikeRegistration(v).orElse(null);
            if (vehicle != null) break;
        }

        if (vehicle == null) {
            throw new RuntimeException("Vehículo no encontrado");
        }

        EntryRecord record = entryRecordRepository.findByVehicleAndStatus(vehicle, "active")
                .orElseThrow(() -> new RuntimeException("No se encontró una entrada activa para este vehículo"));

        Long recordParkingLotId = record.getCell().getParkingLot().getId();
        if (!recordParkingLotId.equals(request.getParkingLotId())) {
            throw new RuntimeException("El vehiculo no se encuentra en este parqueadero");
        }

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
        List<Rate> activeRates = rateRepository
                .findByParkingLotAndVehicleTypeAndActive(parkingLot, vehicle.getVehicleType(), true);

        BigDecimal subtotal = calculateSubtotal(duration, activeRates);

        User owner = vehicle.getOwner();
        BigDecimal discountAmount = (owner != null)
                ? discountService.calculateDiscount(parkingLot, owner, subtotal)
                : BigDecimal.ZERO;
        BigDecimal totalPaid = subtotal.subtract(discountAmount);
        BigDecimal discountPercentage = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0 && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            discountPercentage = discountAmount.multiply(BigDecimal.valueOf(100)).divide(subtotal, java.math.RoundingMode.HALF_UP);
        }

        Payment payment = new Payment();
        payment.setEntryRecord(record);
        payment.setSubtotal(subtotal);
        payment.setDiscountPercentage(discountPercentage);
        payment.setDiscountAmount(discountAmount);
        payment.setTotalPaid(totalPaid);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setIssuedAt(LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);

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
        response.setPaymentMethod(request.getPaymentMethod());
        response.setInvoiceId(invoice.getId());

        return response;
    }

    public List<ActiveEntryResponse> getActiveEntries(Long parkingLotId) {
        return entryRecordRepository
                .findByCell_ParkingLot_IdAndStatusOrderByEntryTimeDesc(parkingLotId, "active")
                .stream()
                .map(er -> {
                    ActiveEntryResponse r = new ActiveEntryResponse();
                    r.setEntryRecordId(er.getId());
                    r.setPlate(er.getVehicle().getPlate());
                    r.setBikeRegistration(er.getVehicle().getBikeRegistration());
                    r.setVehicleType(er.getVehicle().getVehicleType().getName());
                    r.setCellCode(er.getCell().getCode());
                    r.setEntryTime(er.getEntryTime());
                    return r;
                })
                .toList();
    }

    private BigDecimal calculateSubtotal(int duration, List<Rate> activeRates) {
        if (activeRates.isEmpty()) {
            throw new RuntimeException("No se encontró una tarifa activa para este tipo de vehículo");
        }

        Rate flatRate = activeRates.stream()
                .filter(r -> "flat".equals(r.getRateType()))
                .findFirst().orElse(null);
        Rate perMinuteRate = activeRates.stream()
                .filter(r -> "per_minute".equals(r.getRateType()))
                .findFirst().orElse(null);

        if (flatRate != null && duration >= FLAT_RATE_THRESHOLD_MINUTES) {
            return flatRate.getCost();
        }
        if (perMinuteRate != null) {
            return perMinuteRate.getCost().multiply(BigDecimal.valueOf(duration));
        }
        if (flatRate != null) {
            return flatRate.getCost();
        }
        throw new RuntimeException("No se encontró una tarifa activa para este tipo de vehículo");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}