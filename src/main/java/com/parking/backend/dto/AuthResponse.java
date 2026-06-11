package com.parking.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private Long userId;
    private String name;
    private Long parkingLotId;  // null for customers

    public AuthResponse(String token, String role, Long userId, String name, Long parkingLotId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.name = name;
        this.parkingLotId = parkingLotId;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public Long getParkingLotId() { return parkingLotId; }
}