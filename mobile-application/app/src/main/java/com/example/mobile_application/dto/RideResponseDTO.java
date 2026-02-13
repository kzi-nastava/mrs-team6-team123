package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class RideResponseDTO {
    @SerializedName("rideId")
    private Long rideId;

    @SerializedName("driverId")
    private Long driverId;

    @SerializedName("driverName")
    private String driverName;

    @SerializedName("vehicleLicense")
    private String vehicleLicense;

    @SerializedName("status")
    private String status;

    @SerializedName("estimatedTimeMinutes")
    private Integer estimatedTimeMinutes;

    @SerializedName("estimatedPrice")
    private Double estimatedPrice;

    public RideResponseDTO() {
    }

    public RideResponseDTO(Long rideId, Long driverId, String driverName,
            String vehicleLicense, String status,
            Integer estimatedTimeMinutes, Double estimatedPrice) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleLicense = vehicleLicense;
        this.status = status;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.estimatedPrice = estimatedPrice;
    }

    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleLicense() {
        return vehicleLicense;
    }

    public void setVehicleLicense(String vehicleLicense) {
        this.vehicleLicense = vehicleLicense;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
