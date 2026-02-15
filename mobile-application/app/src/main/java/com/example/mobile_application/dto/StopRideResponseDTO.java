
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class StopRideResponseDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("stoppedLocation")
    private String stoppedLocation;
    @SerializedName("stoppedAt")
    private String stoppedAt;
    @SerializedName("recalculatedPrice")
    private double recalculatedPrice;
    @SerializedName("message")
    private String message;

    public StopRideResponseDTO() {}

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public String getStoppedLocation() { return stoppedLocation; }
    public void setStoppedLocation(String stoppedLocation) { this.stoppedLocation = stoppedLocation; }
    public String getStoppedAt() { return stoppedAt; }
    public void setStoppedAt(String stoppedAt) { this.stoppedAt = stoppedAt; }
    public double getRecalculatedPrice() { return recalculatedPrice; }
    public void setRecalculatedPrice(double recalculatedPrice) { this.recalculatedPrice = recalculatedPrice; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
