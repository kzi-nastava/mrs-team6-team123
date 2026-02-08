package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RideInfoDTO {
    @SerializedName("driver")
    private String driver;
    @SerializedName("startedAt")
    private String startedAt;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;
    @SerializedName("price")
    private double price;
    @SerializedName("duration")
    private int duration;
    @SerializedName("passengers")
    private List<String> passengers = new ArrayList<>();
    @SerializedName("reports")
    private List<String> reports = new ArrayList<>();
    @SerializedName("status")
    private String status;

    public RideInfoDTO() {
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
