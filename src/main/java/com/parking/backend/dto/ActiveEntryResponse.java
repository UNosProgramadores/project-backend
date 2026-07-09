package com.parking.backend.dto;

import java.time.LocalDateTime;

public class ActiveEntryResponse {

    private Long entryRecordId;
    private String plate;
    private String bikeRegistration;
    private String vehicleType;
    private String cellCode;
    private LocalDateTime entryTime;

    public Long getEntryRecordId() { return entryRecordId; }
    public void setEntryRecordId(Long entryRecordId) { this.entryRecordId = entryRecordId; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getBikeRegistration() { return bikeRegistration; }
    public void setBikeRegistration(String bikeRegistration) { this.bikeRegistration = bikeRegistration; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
}
