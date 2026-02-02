package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RideRatingResponseDTO {
    private Long rideId;
    private Long driverId;
    private int driverRating;
    private Long vehicleId;
    private int vehicleRating;
    private String comment;
    private Long authorId;
}
