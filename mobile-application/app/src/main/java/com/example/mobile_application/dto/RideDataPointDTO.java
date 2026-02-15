package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class RideDataPointDTO {
    @SerializedName("date")
    private String date;
    @SerializedName("value")
    private double value;

    public RideDataPointDTO() {
    }

    public RideDataPointDTO(String date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
