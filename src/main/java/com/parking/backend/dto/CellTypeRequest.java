package com.parking.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CellTypeRequest {

    @NotBlank(message = "El tipo de celda es requerido")
    private String cellType;

    public String getCellType() {
        return cellType;
    }
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
}
