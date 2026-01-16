package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RideRatingRequestDTO {
    private Long rideId;
    private int driverRating;
    private int vehicleRating;

    public RideRatingRequestDTO() {
    }

}
