package com.parking.backend.dto;

public class VehicleExitRequest {

    private Long parkingLotId;
    private String plate;
    private String bikeRegistration;
    private String paymentMethod;

    public Long getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Long parkingLotId) {
        this.parkingLotId = parkingLotId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}