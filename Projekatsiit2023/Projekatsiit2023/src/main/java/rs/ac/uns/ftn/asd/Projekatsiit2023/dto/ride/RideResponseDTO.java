package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

@Setter
@Getter
public class RideResponseDTO {
    private Long rideId;
    private Long driverId;
    private RideStatus status; // CREATED, ACCEPTED, STARTED, FINISHED
    private Integer estimatedTimeMinutes;
    private Double estimatedPrice;

    public RideResponseDTO() {
    }

}
