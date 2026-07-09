package com.parking.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cell")
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    private Integer row;

    @Column(name = "col")
    private Integer col;

    // Positional code "row-col" (0-indexed), set once at creation, never changes
    private String code;

    // "parking" or "transit"
    @Column(name = "cell_type")
    private String cellType;

    // "available" or "occupied"
    private String status;

    // Null when cellType = "transit"
    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(name = "reserved_for_staff")
    private Boolean reservedForStaff;

    @Column(nullable = false)
    private Boolean active = true;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }
    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Integer getRow() {
        return row;
    }
    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }
    public void setCol(Integer col) {
        this.col = col;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCellType() {
        return cellType;
    }
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getReservedForStaff() {
        return reservedForStaff;
    }
    public void setReservedForStaff(Boolean reservedForStaff) {
        this.reservedForStaff = reservedForStaff;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
}
