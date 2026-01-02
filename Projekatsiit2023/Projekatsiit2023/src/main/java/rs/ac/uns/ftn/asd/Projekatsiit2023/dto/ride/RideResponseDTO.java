package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RideResponseDTO {
    private Long rideId;
    private Long driverId;
    private String status; // CREATED, ACCEPTED, STARTED, FINISHED
    private Integer estimatedTimeMinutes;
    private Double estimatedPrice;

    public RideResponseDTO() {
    }

}
