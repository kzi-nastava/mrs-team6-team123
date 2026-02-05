package com.example.mobile_application.model;

import androidx.annotation.experimental.UseExperimental;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DriverRideHistoryDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("passengers")
    private List<String> passengers = new ArrayList<>();
    @SerializedName("startLocation")
    private String startLocation;
    @SerializedName("endLocation")
    private String endLocation;
    @SerializedName("startedAt")
    private LocalTime startedAt;
    @SerializedName("endedAt")
    private LocalTime endedAt;
    @SerializedName("date")
    private LocalDate date;
    @SerializedName("price")
    private double price;
    @SerializedName("panicTriggered")
    private String panicTriggered;
    @SerializedName("canceledBy")
    private String canceledBy;
    @SerializedName("startLat")
    private double startLat;
    @SerializedName("startLng")
    private double startLng;
    @SerializedName("endLat")
    private double endLat;
    @SerializedName("endLng")
    private double endLng;
    @SerializedName("reports")
    private List<String> reports = new ArrayList<>();
    @SerializedName("stops")
    private List<GeoPointDTO> stops = new ArrayList<>();

    public DriverRideHistoryDTO() {}

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
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

    public LocalTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalTime endedAt) {
        this.endedAt = endedAt;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(String panicTriggered) {
        this.panicTriggered = panicTriggered;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(String canceledBy) {
        this.canceledBy = canceledBy;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }

    public List<GeoPointDTO> getStops() {
        return stops;
    }

    public void setStops(List<GeoPointDTO> stops) {
        this.stops = stops;
    }
}
