package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.util.List;

@Setter
@Getter
public class RideEstimationRequestDTO {
    @JsonProperty("startLocation")
    private String startLocation;
    @JsonProperty("endLocation")
    private String endLocation;
    @JsonProperty("intermediateStops")
    private List<String> intermediateStops;
    @JsonProperty("vehicleType")
    private VehicleType vehicleType;

    public RideEstimationRequestDTO() {
    }

}