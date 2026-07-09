package com.parking.backend.dto;

import com.parking.backend.entity.Vehicle;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects null parkingLotId")
    void vehicleEntryRejectsNullParkingLotId() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setVehicleTypeId(1L);
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("parkingLotId")));
    }

    @Test
    @DisplayName("VehicleExitRequest rejects null parkingLotId")
    void vehicleExitRejectsNullParkingLotId() {
        VehicleExitRequest req = new VehicleExitRequest();
        req.setPaymentMethod("cash");
        Set<ConstraintViolation<VehicleExitRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("parkingLotId")));
    }

    @Test
    @DisplayName("VehicleExitRequest rejects blank paymentMethod")
    void vehicleExitRejectsBlankPaymentMethod() {
        VehicleExitRequest req = new VehicleExitRequest();
        req.setParkingLotId(1L);
        req.setPaymentMethod("");
        Set<ConstraintViolation<VehicleExitRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentMethod")));
    }

    @Test
    @DisplayName("DiscountConfigRequest rejects null discountPercentage")
    void discountConfigRejectsNullDiscountPercentage() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setActive(true);
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountPercentage")));
    }

    @Test
    @DisplayName("DiscountConfigRequest rejects discountPercentage > 100")
    void discountConfigRejectsOver100() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("150"));
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountPercentage")));
    }

    @Test
    @DisplayName("RateRequest rejects null cost, rateType, vehicleTypeId")
    void rateRequestRejectsNullFields() {
        RateRequest req = new RateRequest();
        Set<ConstraintViolation<RateRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cost")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rateType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("vehicleTypeId")));
    }

    @Test
    @DisplayName("RateRequest rejects negative cost")
    void rateRequestRejectsNegativeCost() {
        RateRequest req = new RateRequest();
        req.setVehicleTypeId(1L);
        req.setRateType("flat");
        req.setCost(new BigDecimal("-10"));
        Set<ConstraintViolation<RateRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cost")));
    }

    @Test
    @DisplayName("CellTypeRequest rejects blank cellType")
    void cellTypeRequestRejectsBlank() {
        CellTypeRequest req = new CellTypeRequest();
        req.setCellType("");
        Set<ConstraintViolation<CellTypeRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cellType")));
    }

    @Test
    @DisplayName("CellVehicleTypeRequest rejects null vehicleTypeId")
    void cellVehicleTypeRejectsNull() {
        CellVehicleTypeRequest req = new CellVehicleTypeRequest();
        Set<ConstraintViolation<CellVehicleTypeRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("vehicleTypeId")));
    }

    // ──────────────────────────────────────────────
    // Nuevas validaciones RF_17
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("StaffRequest rejects blank phone")
    void staffRequestRejectsBlankPhone() {
        StaffRequest req = new StaffRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("");
        req.setUsername("user");
        req.setPassword("pass");
        req.setParkingLotId(1L);
        Set<ConstraintViolation<StaffRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("RegisterRequest rejects blank phone")
    void registerRequestRejectsBlankPhone() {
        RegisterRequest req = new RegisterRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("");
        req.setUsername("user");
        req.setPassword("pass");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects both plate and bikeRegistration missing")
    void vehicleEntryRejectsMissingPlateAndBikeReg() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("VehicleEntryRequest accepts plate without bikeRegistration")
    void vehicleEntryAcceptsPlateOnly() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        req.setPlate("ABC123");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("VehicleEntryRequest accepts bikeRegistration without plate")
    void vehicleEntryAcceptsBikeRegOnly() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        req.setBikeRegistration("BIC123");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("VehicleExitRequest rejects both plate and bikeRegistration missing")
    void vehicleExitRejectsMissingPlateAndBikeReg() {
        VehicleExitRequest req = new VehicleExitRequest();
        req.setParkingLotId(1L);
        req.setPaymentMethod("cash");
        Set<ConstraintViolation<VehicleExitRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("ClaimVehicleRequest rejects both plate and bikeRegistration missing")
    void claimVehicleRejectsMissingPlateAndBikeReg() {
        ClaimVehicleRequest req = new ClaimVehicleRequest();
        Set<ConstraintViolation<ClaimVehicleRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("ClaimVehicleRequest accepts plate only")
    void claimVehicleAcceptsPlateOnly() {
        ClaimVehicleRequest req = new ClaimVehicleRequest();
        req.setPlate("ABC123");
        Set<ConstraintViolation<ClaimVehicleRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("plateOrBikeRegistrationProvided")));
    }

    @Test
    @DisplayName("DiscountConfigRequest rejects null active")
    void discountConfigRejectsNullActive() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("10"));
        req.setMinExternalInvoice(new BigDecimal("100"));
        req.setStartDate(java.time.LocalDateTime.now());
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("active")));
    }

    @Test
    @DisplayName("DiscountConfigRequest rejects null startDate")
    void discountConfigRejectsNullStartDate() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("10"));
        req.setActive(true);
        req.setMinExternalInvoice(new BigDecimal("100"));
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("startDate")));
    }

    @Test
    @DisplayName("DiscountConfigRequest rejects both minExternalInvoice and minVisits missing")
    void discountConfigRejectsMissingMinInvoiceAndMinVisits() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("10"));
        req.setActive(true);
        req.setStartDate(java.time.LocalDateTime.now());
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("minExternalInvoiceOrMinVisitsProvided")));
    }

    @Test
    @DisplayName("DiscountConfigRequest accepts minExternalInvoice without minVisits")
    void discountConfigAcceptsMinExternalInvoiceOnly() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("10"));
        req.setActive(true);
        req.setStartDate(java.time.LocalDateTime.now());
        req.setMinExternalInvoice(new BigDecimal("100"));
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("minExternalInvoiceOrMinVisitsProvided")));
    }

    @Test
    @DisplayName("DiscountConfigRequest accepts minVisits without minExternalInvoice")
    void discountConfigAcceptsMinVisitsOnly() {
        DiscountConfigRequest req = new DiscountConfigRequest();
        req.setDiscountPercentage(new BigDecimal("10"));
        req.setActive(true);
        req.setStartDate(java.time.LocalDateTime.now());
        req.setMinVisits(5);
        Set<ConstraintViolation<DiscountConfigRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("minExternalInvoiceOrMinVisitsProvided")));
    }

    @Test
    @DisplayName("LoginRequest rejects blank username")
    void loginRejectsBlankUsername() {
        LoginRequest req = new LoginRequest();
        req.setUsername("");
        req.setPassword("pass");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("LoginRequest rejects blank password")
    void loginRejectsBlankPassword() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("ParkingLotRequest validates rows and columns boundaries")
    void parkingLotValidatesRowsColumns() {
        ParkingLotRequest req = new ParkingLotRequest();
        req.setName("Test");
        req.setAddress("Addr");
        req.setOpeningTime(java.time.LocalTime.of(8, 0));
        req.setClosingTime(java.time.LocalTime.of(20, 0));
        req.setRows(0);
        req.setColumns(200);
        Set<ConstraintViolation<ParkingLotRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rows")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("columns")));
    }

    // ──────────────────────────────────────────────
    // Validación de formato de placa y registro
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("VehicleEntryRequest accepts valid car plate ABC123")
    void vehicleEntryAcceptsValidCarPlate() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        req.setPlate("ABC123");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("carPlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects invalid car plate ABCDEF (no digits)")
    void vehicleEntryRejectsInvalidCarPlate() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        req.setPlate("ABCDEF");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("carPlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects car plate ABC78A (moto format)")
    void vehicleEntryRejectsMotoPlateForCar() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(1L);
        req.setPlate("ABC78A");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("carPlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest accepts valid motorcycle plate ABC78A")
    void vehicleEntryAcceptsValidMotorcyclePlate() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(2L);
        req.setPlate("ABC78A");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("motorcyclePlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects invalid motorcycle plate ABC123 (car format)")
    void vehicleEntryRejectsCarPlateForMotorcycle() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(2L);
        req.setPlate("ABC123");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("motorcyclePlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects invalid motorcycle plate ABCDEFG (all letters)")
    void vehicleEntryRejectsInvalidMotorcyclePlate() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(2L);
        req.setPlate("ABCDEFG");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("motorcyclePlateFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest accepts valid bike registration ABCDEFG")
    void vehicleEntryAcceptsValidBikeReg() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(3L);
        req.setBikeRegistration("ABCDEFG");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("bikeRegistrationFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects bike registration with digits ABC1234")
    void vehicleEntryRejectsBikeRegWithDigits() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(3L);
        req.setBikeRegistration("ABC1234");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bikeRegistrationFormatValid")));
    }

    @Test
    @DisplayName("VehicleEntryRequest rejects bike registration too short ABCDEF")
    void vehicleEntryRejectsBikeRegTooShort() {
        VehicleEntryRequest req = new VehicleEntryRequest();
        req.setParkingLotId(1L);
        req.setVehicleTypeId(3L);
        req.setBikeRegistration("ABCDEF");
        Set<ConstraintViolation<VehicleEntryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bikeRegistrationFormatValid")));
    }

    // ──────────────────────────────────────────────
    // Validación de formato de teléfono
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("RegisterRequest accepts valid 10-digit phone")
    void registerRequestAcceptsValidPhone() {
        RegisterRequest req = new RegisterRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("3001234567");
        req.setUsername("user");
        req.setPassword("pass");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("RegisterRequest rejects phone with 9 digits")
    void registerRequestRejectsPhone9Digits() {
        RegisterRequest req = new RegisterRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("300123456");
        req.setUsername("user");
        req.setPassword("pass");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("RegisterRequest rejects phone with letters")
    void registerRequestRejectsPhoneWithLetters() {
        RegisterRequest req = new RegisterRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("300ABC4567");
        req.setUsername("user");
        req.setPassword("pass");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("StaffRequest accepts valid 10-digit phone")
    void staffRequestAcceptsValidPhone() {
        StaffRequest req = new StaffRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("3109876543");
        req.setUsername("user");
        req.setPassword("pass");
        req.setParkingLotId(1L);
        Set<ConstraintViolation<StaffRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("StaffRequest rejects phone with 11 digits")
    void staffRequestRejectsPhone11Digits() {
        StaffRequest req = new StaffRequest();
        req.setDocument("123");
        req.setName("Test");
        req.setPhone("31098765432");
        req.setUsername("user");
        req.setPassword("pass");
        req.setParkingLotId(1L);
        Set<ConstraintViolation<StaffRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    // ──────────────────────────────────────────────
    // Normalización de Vehicle (trim + uppercase)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("Vehicle.setPlate normaliza a mayúsculas y sin espacios")
    void vehicleSetPlateNormalizes() {
        Vehicle v = new Vehicle();
        v.setPlate("  abc-123  ");
        assertEquals("ABC-123", v.getPlate());
    }

    @Test
    @DisplayName("Vehicle.setPlate acepta null")
    void vehicleSetPlateAcceptsNull() {
        Vehicle v = new Vehicle();
        v.setPlate(null);
        assertNull(v.getPlate());
    }

    @Test
    @DisplayName("Vehicle.setBikeRegistration normaliza a mayúsculas")
    void vehicleSetBikeRegNormalizes() {
        Vehicle v = new Vehicle();
        v.setBikeRegistration("  bike-001  ");
        assertEquals("BIKE-001", v.getBikeRegistration());
    }

    @Test
    @DisplayName("Vehicle.setBikeRegistration acepta null")
    void vehicleSetBikeRegAcceptsNull() {
        Vehicle v = new Vehicle();
        v.setBikeRegistration(null);
        assertNull(v.getBikeRegistration());
    }
}
