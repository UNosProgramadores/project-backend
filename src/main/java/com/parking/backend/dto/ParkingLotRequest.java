package com.parking.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class ParkingLotRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime openingTime;

    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime closingTime;

    @NotNull(message = "El número de filas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 fila")
    @Max(value = 100, message = "El número de filas no puede superar 100")
    private Integer rows;

    @NotNull(message = "El número de columnas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 columna")
    @Max(value = 100, message = "El número de columnas no puede superar 100")
    private Integer columns;

    private Boolean autoAssignment = false;
    private Boolean discountsEnabled = false;

    // Getters y Setters
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
