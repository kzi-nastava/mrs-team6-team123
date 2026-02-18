package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class ChangePriceDTO {
    @SerializedName("vehicleType")
    private String vehicleType;
    @SerializedName("price")
    private double price;
    @SerializedName("newPrice")
    private double newPrice;

    public ChangePriceDTO() {
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

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}
