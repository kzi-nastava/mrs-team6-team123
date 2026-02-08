package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class RideMonitoringDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("driverId")
    private Long driverId;
    @SerializedName("driverName")
    private String driverName;
    @SerializedName("licencePlate")
    private String licencePlate;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;

    public RideMonitoringDTO() {
    }

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

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
