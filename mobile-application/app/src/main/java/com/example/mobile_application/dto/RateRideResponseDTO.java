package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class RateRideResponseDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("driverId")
    private Long driverId;
    @SerializedName("driverRating")
    private int driverRating;
    @SerializedName("vehicleId")
    private Long vehicleId;
    @SerializedName("vehicleRating")
    private int vehicleRating;
    @SerializedName("comment")
    private String comment;
    @SerializedName("authorId")
    private Long authorId;

    public RateRideResponseDTO() {
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

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(int vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
