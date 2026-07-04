package com.parking.backend.dto;

public class CellDto {

    private Long id;
    private Integer row;
    private Integer col;
    private String code;
    private String cellType;
    private String status;
    private Long vehicleTypeId;
    private String vehicleTypeName;
    private Boolean reservedForStaff;

    public CellDto() {}

    public CellDto(Long id, Integer row, Integer col, String code, String cellType,
                   String status, Long vehicleTypeId, String vehicleTypeName,
                   Boolean reservedForStaff) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.code = code;
        this.cellType = cellType;
        this.status = status;
        this.vehicleTypeId = vehicleTypeId;
        this.vehicleTypeName = vehicleTypeName;
        this.reservedForStaff = reservedForStaff;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRow() { return row; }
    public void setRow(Integer row) { this.row = row; }

    public Integer getCol() { return col; }
    public void setCol(Integer col) { this.col = col; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCellType() { return cellType; }
    public void setCellType(String cellType) { this.cellType = cellType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getVehicleTypeId() { return vehicleTypeId; }
    public void setVehicleTypeId(Long vehicleTypeId) { this.vehicleTypeId = vehicleTypeId; }

    public String getVehicleTypeName() { return vehicleTypeName; }
    public void setVehicleTypeName(String vehicleTypeName) { this.vehicleTypeName = vehicleTypeName; }

    public Boolean getReservedForStaff() { return reservedForStaff; }
    public void setReservedForStaff(Boolean reservedForStaff) { this.reservedForStaff = reservedForStaff; }
}
