package com.parking.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportResponse {

    private Long parkingLotId;
    private String parkingLotName;
    private String period;
    private String referenceDate;
    private String startDate;
    private String endDate;
    private BigDecimal totalRevenue;
    private long totalEntries;
    private long totalExits;
    private List<VehicleTypeCount> entriesByVehicleType;
    private List<VehicleTypeCount> exitsByVehicleType;
    private List<StaffActivity> staffActivity;

    public Long getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(Long parkingLotId) { this.parkingLotId = parkingLotId; }

    public String getParkingLotName() { return parkingLotName; }
    public void setParkingLotName(String parkingLotName) { this.parkingLotName = parkingLotName; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getReferenceDate() { return referenceDate; }
    public void setReferenceDate(String referenceDate) { this.referenceDate = referenceDate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTotalEntries() { return totalEntries; }
    public void setTotalEntries(long totalEntries) { this.totalEntries = totalEntries; }

    public long getTotalExits() { return totalExits; }
    public void setTotalExits(long totalExits) { this.totalExits = totalExits; }

    public List<VehicleTypeCount> getEntriesByVehicleType() { return entriesByVehicleType; }
    public void setEntriesByVehicleType(List<VehicleTypeCount> entriesByVehicleType) { this.entriesByVehicleType = entriesByVehicleType; }

    public List<VehicleTypeCount> getExitsByVehicleType() { return exitsByVehicleType; }
    public void setExitsByVehicleType(List<VehicleTypeCount> exitsByVehicleType) { this.exitsByVehicleType = exitsByVehicleType; }

    public List<StaffActivity> getStaffActivity() { return staffActivity; }
    public void setStaffActivity(List<StaffActivity> staffActivity) { this.staffActivity = staffActivity; }

    public static class StaffActivity {
        private Long staffId;
        private String staffName;
        private long entriesRecorded;
        private long exitsRecorded;

        public StaffActivity() {}

        public StaffActivity(Long staffId, String staffName, long entriesRecorded, long exitsRecorded) {
            this.staffId = staffId;
            this.staffName = staffName;
            this.entriesRecorded = entriesRecorded;
            this.exitsRecorded = exitsRecorded;
        }

        public Long getStaffId() { return staffId; }
        public void setStaffId(Long staffId) { this.staffId = staffId; }

        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }

        public long getEntriesRecorded() { return entriesRecorded; }
        public void setEntriesRecorded(long entriesRecorded) { this.entriesRecorded = entriesRecorded; }

        public long getExitsRecorded() { return exitsRecorded; }
        public void setExitsRecorded(long exitsRecorded) { this.exitsRecorded = exitsRecorded; }
    }

    public static class VehicleTypeCount {
        private String vehicleType;
        private long count;

        public VehicleTypeCount() {}

        public VehicleTypeCount(String vehicleType, long count) {
            this.vehicleType = vehicleType;
            this.count = count;
        }

        public String getVehicleType() { return vehicleType; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}
