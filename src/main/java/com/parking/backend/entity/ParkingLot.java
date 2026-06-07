package com.parking.backend.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "parking_lot")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    // LocalTime: maps to SQL type TIME — stores only the time of day, no date (e.g. 06:00)
    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    private Integer rows;
    private Integer columns;

    @Column(name = "auto_assignment")
    private Boolean autoAssignment;

    @Column(name = "discounts_enabled")
    private Boolean discountsEnabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public Integer getRows() { return rows; }
    public void setRows(Integer rows) { this.rows = rows; }

    public Integer getColumns() { return columns; }
    public void setColumns(Integer columns) { this.columns = columns; }

    public Boolean getAutoAssignment() { return autoAssignment; }
    public void setAutoAssignment(Boolean autoAssignment) { this.autoAssignment = autoAssignment; }

    public Boolean getDiscountsEnabled() { return discountsEnabled; }
    public void setDiscountsEnabled(Boolean discountsEnabled) { this.discountsEnabled = discountsEnabled; }
}