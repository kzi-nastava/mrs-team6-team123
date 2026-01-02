package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import java.util.List;

public class RideEstimationRequestDTO {
    private String startLocation;
    private String endLocation;
    private List<String> intermediateStops;
    private String vehicleType;

    public RideEstimationRequestDTO() {
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

    public List<String> getIntermediateStops() {
        return intermediateStops;
    }

    public void setIntermediateStops(List<String> intermediateStops) {
        this.intermediateStops = intermediateStops;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}