package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RideOrderRequestDTO {
    @SerializedName("creatorId")
    private Long creatorId;

    @SerializedName("passengerIds")
    private List<Long> passengerIds;

    @SerializedName("startLocation")
    private String startLocation;

    @SerializedName("endLocation")
    private String endLocation;

    @SerializedName("startLatitude")
    private Double startLatitude;

    @SerializedName("startLongitude")
    private Double startLongitude;

    @SerializedName("endLatitude")
    private Double endLatitude;

    @SerializedName("endLongitude")
    private Double endLongitude;

    @SerializedName("waypoints")
    private List<String> waypoints;

    @SerializedName("scheduledAt")
    private String scheduledAt;

    @SerializedName("babySeat")
    private Boolean babySeat;

    @SerializedName("petFriendly")
    private Boolean petFriendly;

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("additionalInstructions")
    private String additionalInstructions;

    @SerializedName("estimatedPrice")
    private Double estimatedPrice;

    public RideOrderRequestDTO() {
    }

    public RideOrderRequestDTO(Long creatorId, List<Long> passengerIds, String startLocation,
            String endLocation, Double startLatitude, Double startLongitude,
            Double endLatitude, Double endLongitude, List<String> waypoints,
            String scheduledAt, Boolean babySeat, Boolean petFriendly,
            String vehicleType, String additionalInstructions, Double estimatedPrice) {
        this.creatorId = creatorId;
        this.passengerIds = passengerIds;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.waypoints = waypoints;
        this.scheduledAt = scheduledAt;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.vehicleType = vehicleType;
        this.additionalInstructions = additionalInstructions;
        this.estimatedPrice = estimatedPrice;
    }

    // Getters and Setters
    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Long> getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(List<Long> passengerIds) {
        this.passengerIds = passengerIds;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Boolean getBabySeat() {
        return babySeat;
    }

    public void setBabySeat(Boolean babySeat) {
        this.babySeat = babySeat;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
