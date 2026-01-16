package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RideRatingResponseDTO {
    private Long rideId;
    private Long driverId;
    private int driverRating;
    private Long vehicleId;
    private int vehicleRating;

    public RideRatingResponseDTO() {
    }

}
