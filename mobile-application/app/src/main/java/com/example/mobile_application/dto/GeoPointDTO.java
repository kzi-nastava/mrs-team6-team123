package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoPointDTO implements Serializable {
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("location")
    private String location;

    public GeoPointDTO() {}

    public GeoPointDTO(double latitude, double longitude, String location) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
