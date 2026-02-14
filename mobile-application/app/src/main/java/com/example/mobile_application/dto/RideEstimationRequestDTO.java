
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import com.example.mobile_application.enums.VehicleType;
import java.util.List;

public class RideEstimationRequestDTO {
    @SerializedName("startLocation")
    private String startLocation;
    @SerializedName("endLocation")
    private String endLocation;
    @SerializedName("intermediateStops")
    private List<String> intermediateStops;
    @SerializedName("vehicleType")
    private VehicleType vehicleType;

    public RideEstimationRequestDTO() {
    }

    public RideEstimationRequestDTO(String startLocation, String endLocation,
            List<String> intermediateStops, VehicleType vehicleType) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.intermediateStops = intermediateStops;
        this.vehicleType = vehicleType;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String s) {
        this.startLocation = s;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String s) {
        this.endLocation = s;
    }

    public List<String> getIntermediateStops() {
        return intermediateStops;
    }

    public void setIntermediateStops(List<String> s) {
        this.intermediateStops = s;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType v) {
        this.vehicleType = v;
    }
}
