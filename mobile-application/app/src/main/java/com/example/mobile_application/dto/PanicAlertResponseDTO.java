
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class PanicAlertResponseDTO {
    @SerializedName("id")
    private Long id;
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("triggeredByUserId")
    private Long triggeredByUserId;
    @SerializedName("triggeredByName")
    private String triggeredByName;
    @SerializedName("currentLocation")
    private String currentLocation;
    @SerializedName("triggeredAt")
    private String triggeredAt;
    @SerializedName("resolved")
    private boolean resolved;

    public PanicAlertResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getTriggeredByUserId() { return triggeredByUserId; }
    public void setTriggeredByUserId(Long triggeredByUserId) { this.triggeredByUserId = triggeredByUserId; }
    public String getTriggeredByName() { return triggeredByName; }
    public void setTriggeredByName(String triggeredByName) { this.triggeredByName = triggeredByName; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(String triggeredAt) { this.triggeredAt = triggeredAt; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}