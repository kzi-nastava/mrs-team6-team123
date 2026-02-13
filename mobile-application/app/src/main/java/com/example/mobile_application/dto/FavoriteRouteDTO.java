package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FavoriteRouteDTO implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("routeId")
    private Long routeId;

    @SerializedName("startLocation")
    private String startLocation;

    @SerializedName("endLocation")
    private String endLocation;

    @SerializedName("startLatitude")
    private double startLatitude;

    @SerializedName("startLongitude")
    private double startLongitude;

    @SerializedName("endLatitude")
    private double endLatitude;

    @SerializedName("endLongitude")
    private double endLongitude;

    @SerializedName("createdAt")
    private String createdAt;

    public FavoriteRouteDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
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

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
