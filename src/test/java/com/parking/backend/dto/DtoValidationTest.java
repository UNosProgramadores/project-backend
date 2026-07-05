package com.parking.backend.dto;

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
}
