package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StatisticsDTO {
    @SerializedName("totalRides")
    private int totalRides;
    @SerializedName("avgRidesPerDay")
    private double avgRidesPerDay;
    @SerializedName("ridesData")
    private List<RideDataPointDTO> ridesData;

    @SerializedName("totalKmTraveled")
    private double totalKmTraveled;
    @SerializedName("avgKmPerDay")
    private double avgKmPerDay;
    @SerializedName("kmData")
    private List<RideDataPointDTO> kmData;

    @SerializedName("totalAmountSpent")
    private double totalAmountSpent;
    @SerializedName("avgAmountPerDay")
    private double avgAmountPerDay;
    @SerializedName("amountData")
    private List<RideDataPointDTO> amountData;

    public StatisticsDTO() {
    }

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public double getAvgRidesPerDay() {
        return avgRidesPerDay;
    }

    public void setAvgRidesPerDay(double avgRidesPerDay) {
        this.avgRidesPerDay = avgRidesPerDay;
    }

    public List<RideDataPointDTO> getRidesData() {
        return ridesData;
    }

    public void setRidesData(List<RideDataPointDTO> ridesData) {
        this.ridesData = ridesData;
    }

    public double getTotalKmTraveled() {
        return totalKmTraveled;
    }

    public void setTotalKmTraveled(double totalKmTraveled) {
        this.totalKmTraveled = totalKmTraveled;
    }

    public double getAvgKmPerDay() {
        return avgKmPerDay;
    }

    public void setAvgKmPerDay(double avgKmPerDay) {
        this.avgKmPerDay = avgKmPerDay;
    }

    public List<RideDataPointDTO> getKmData() {
        return kmData;
    }

    public void setKmData(List<RideDataPointDTO> kmData) {
        this.kmData = kmData;
    }

    public double getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(double totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public double getAvgAmountPerDay() {
        return avgAmountPerDay;
    }

    public void setAvgAmountPerDay(double avgAmountPerDay) {
        this.avgAmountPerDay = avgAmountPerDay;
    }

    public List<RideDataPointDTO> getAmountData() {
        return amountData;
    }

    public void setAmountData(List<RideDataPointDTO> amountData) {
        this.amountData = amountData;
    }
}
