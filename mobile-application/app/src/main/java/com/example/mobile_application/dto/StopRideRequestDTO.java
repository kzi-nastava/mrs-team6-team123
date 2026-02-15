
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class StopRideRequestDTO {
    @SerializedName("currentLocation")
    private String currentLocation;
    @SerializedName("stoppedAt")
    private String stoppedAt;

    public StopRideRequestDTO() {}

    public StopRideRequestDTO(String currentLocation, String stoppedAt) {
        this.currentLocation = currentLocation;
        this.stoppedAt = stoppedAt;
    }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getStoppedAt() { return stoppedAt; }
    public void setStoppedAt(String stoppedAt) { this.stoppedAt = stoppedAt; }
}
