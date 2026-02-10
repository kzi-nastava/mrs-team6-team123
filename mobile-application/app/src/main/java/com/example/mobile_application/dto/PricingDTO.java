package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class PricingDTO {
    @SerializedName("vehicleType")
    private String vehicleType;
    @SerializedName("price")
    private double price;

    public PricingDTO() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
