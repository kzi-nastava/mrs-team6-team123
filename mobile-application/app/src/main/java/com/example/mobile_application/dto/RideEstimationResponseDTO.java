
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class RideEstimationResponseDTO {
    @SerializedName("startLocation")
    private String startLocation;
    @SerializedName("endLocation")
    private String endLocation;
    @SerializedName("estimatedDistance")
    private double estimatedDistance;
    @SerializedName("estimatedTime")
    private int estimatedTime;
    @SerializedName("estimatedPrice")
    private double estimatedPrice;
    @SerializedName("route")
    private String route;

    public RideEstimationResponseDTO() {}

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String s) { this.startLocation = s; }
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String s) { this.endLocation = s; }
    public double getEstimatedDistance() { return estimatedDistance; }
    public void setEstimatedDistance(double d) { this.estimatedDistance = d; }
    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int t) { this.estimatedTime = t; }
    public double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(double p) { this.estimatedPrice = p; }
    public String getRoute() { return route; }
    public void setRoute(String r) { this.route = r; }
}
