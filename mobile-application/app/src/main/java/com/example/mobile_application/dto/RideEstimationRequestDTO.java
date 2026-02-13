
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RideEstimationRequestDTO {
    @SerializedName("startLocation")
    private String startLocation;
    @SerializedName("endLocation")
    private String endLocation;
    @SerializedName("intermediateStops")
    private List<String> intermediateStops;
    @SerializedName("vehicleType")
    private String vehicleType;

    public RideEstimationRequestDTO() {}

    public RideEstimationRequestDTO(String startLocation, String endLocation,
                                    List<String> intermediateStops, String vehicleType) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.intermediateStops = intermediateStops;
        this.vehicleType = vehicleType;
    }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String s) { this.startLocation = s; }
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String s) { this.endLocation = s; }
    public List<String> getIntermediateStops() { return intermediateStops; }
    public void setIntermediateStops(List<String> s) { this.intermediateStops = s; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String s) { this.vehicleType = s; }
}
