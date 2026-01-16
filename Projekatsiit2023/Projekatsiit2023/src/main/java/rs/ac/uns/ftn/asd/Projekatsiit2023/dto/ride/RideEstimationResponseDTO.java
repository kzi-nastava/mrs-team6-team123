package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RideEstimationResponseDTO {
    private String startLocation;
    private String endLocation;
    private double estimatedDistance;
    private int estimatedTime;
    private double estimatedPrice;
    private String route;

    public RideEstimationResponseDTO() {
    }

}