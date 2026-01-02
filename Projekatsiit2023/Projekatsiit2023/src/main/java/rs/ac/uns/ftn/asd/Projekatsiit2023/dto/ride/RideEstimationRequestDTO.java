package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.util.List;

@Setter
@Getter
public class RideEstimationRequestDTO {
    private String startLocation;
    private String endLocation;
    private List<String> intermediateStops;
    private VehicleType vehicleType;

    public RideEstimationRequestDTO() {
    }

}