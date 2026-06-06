package com.parking.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entry_record")
public class EntryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "cell_id", nullable = false)
    private Cell cell;

    // LocalDateTime: exact moment the vehicle entered
    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    // Null while the vehicle is still inside
    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    // Duration in minutes — calculated on exit (RF_13)
    // Persisted so invoices and reports don't need to recalculate
    private Integer duration;

    // "active" or "completed"
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Cell getCell() { return cell; }
    public void setCell(Cell cell) { this.cell = cell; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}