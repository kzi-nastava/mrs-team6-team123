
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class PanicAlertRequestDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("currentLocation")
    private String currentLocation;

    public PanicAlertRequestDTO() {}

    public PanicAlertRequestDTO(Long rideId, Long userId, String currentLocation) {
        this.rideId = rideId;
        this.userId = userId;
        this.currentLocation = currentLocation;
    }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
}