package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RideTrackingResponseDTO {
    private Long rideId;
    private String currentLocation;
    private String nextStop;
    private int timeLeft;

    public RideTrackingResponseDTO() {
    }

}
