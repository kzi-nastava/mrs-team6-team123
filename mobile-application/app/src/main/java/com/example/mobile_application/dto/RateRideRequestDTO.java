package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RateRideRequestDTO implements Serializable {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("driverId")
    private Long driverId;
    @SerializedName("vehicleId")
    private Long vehicleId;
    @SerializedName("driver")
    private String driver;
    @SerializedName("licencePlate")
    private String licencePlate;

    public RateRideRequestDTO() {
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
}
