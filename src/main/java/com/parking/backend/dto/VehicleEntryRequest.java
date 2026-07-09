package com.parking.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class VehicleEntryRequest {

    @NotNull(message = "El id del parqueadero es requerido")
    private Long parkingLotId;

    @NotNull(message = "El tipo de vehículo es requerido")
    private Long vehicleTypeId;

    private String plate;
    private String bikeRegistration;
    private String ownerDocument;
    private Long cellId;

    @AssertTrue(message = "Debe proporcionar placa (para vehículos motorizados) o registro de bicicleta")
    private boolean isPlateOrBikeRegistrationProvided() {
        return (plate != null && !plate.isBlank()) || (bikeRegistration != null && !bikeRegistration.isBlank());
    }

    @AssertTrue(message = "La placa de carro debe tener el formato ABC123 (3 letras + 3 dígitos)")
    private boolean isCarPlateFormatValid() {
        if (plate == null || plate.isBlank()) return true;
        if (vehicleTypeId == null || vehicleTypeId != 1L) return true;
        return plate.trim().toUpperCase().matches("^[A-Z]{3}[0-9]{3}$");
    }

    @AssertTrue(message = "La placa de moto debe tener el formato ABC78A (3 letras + 2 dígitos + 1 letra)")
    private boolean isMotorcyclePlateFormatValid() {
        if (plate == null || plate.isBlank()) return true;
        if (vehicleTypeId == null || vehicleTypeId != 2L) return true;
        return plate.trim().toUpperCase().matches("^[A-Z]{3}[0-9]{2}[A-Z]$");
    }

    @AssertTrue(message = "El registro de bicicleta debe tener 7 letras (ej. ABCDEFG)")
    private boolean isBikeRegistrationFormatValid() {
        if (bikeRegistration == null || bikeRegistration.isBlank()) return true;
        return bikeRegistration.trim().toUpperCase().matches("^[A-Z]{7}$");
    }

    public Long getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Long parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBikeRegistration() {
        return bikeRegistration;
    }

    public void setBikeRegistration(String bikeRegistration) {
        this.bikeRegistration = bikeRegistration;
    }

    public String getOwnerDocument() {
        return ownerDocument;
    }

    public void setOwnerDocument(String ownerDocument) {
        this.ownerDocument = ownerDocument;
    }

    public Long getCellId() {
        return cellId;
    }

    public void setCellId(Long cellId) {
        this.cellId = cellId;
    }
}
