package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RideRatingRequestDTO {
    private Long rideId;
    private Long driverId;
    private Long vehicleId;
    private String driver;
    private String licencePlate;
}
