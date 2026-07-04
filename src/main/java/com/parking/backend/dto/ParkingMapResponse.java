package com.parking.backend.dto;

import java.util.List;

public class ParkingMapResponse {

    private Long parkingLotId;
    private String parkingLotName;
    private Integer rows;
    private Integer columns;
    private List<List<CellDto>> grid;

    public ParkingMapResponse() {}

    public ParkingMapResponse(Long parkingLotId, String parkingLotName, Integer rows,
                              Integer columns, List<List<CellDto>> grid) {
        this.parkingLotId = parkingLotId;
        this.parkingLotName = parkingLotName;
        this.rows = rows;
        this.columns = columns;
        this.grid = grid;
    }

    public Long getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(Long parkingLotId) { this.parkingLotId = parkingLotId; }

    public String getParkingLotName() { return parkingLotName; }
    public void setParkingLotName(String parkingLotName) { this.parkingLotName = parkingLotName; }

    public Integer getRows() { return rows; }
    public void setRows(Integer rows) { this.rows = rows; }

    public Integer getColumns() { return columns; }
    public void setColumns(Integer columns) { this.columns = columns; }

    public List<List<CellDto>> getGrid() { return grid; }
    public void setGrid(List<List<CellDto>> grid) { this.grid = grid; }
}
