package com.parking.backend.dto;

public class VehicleEntryRequest {

    private Long parkingLotId;
    private Long vehicleTypeId;
    private String plate;
    private String bikeRegistration;
    private Long cellId;

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

    public Long getCellId() {
        return cellId;
    }

    public void setCellId(Long cellId) {
        this.cellId = cellId;
    }
}