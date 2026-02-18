package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrackRideDTO implements Serializable {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("driverId")
    private Long driverId;
    @SerializedName("stops")
    private List<GeoPointDTO> stops = new ArrayList<>();
    @SerializedName("stopsMade")
    private int stopsMade = 0;
    @SerializedName("info")
    private RideInfoDTO info = new RideInfoDTO();

    public TrackRideDTO() {
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

    public List<GeoPointDTO> getStops() {
        return stops;
    }

    public void setStops(List<GeoPointDTO> stops) {
        this.stops = stops;
    }

    public int getStopsMade() {
        return stopsMade;
    }

    public void setStopsMade(int stopsMade) {
        this.stopsMade = stopsMade;
    }

    public RideInfoDTO getInfo() {
        return info;
    }

    public void setInfo(RideInfoDTO info) {
        this.info = info;
    }
}
