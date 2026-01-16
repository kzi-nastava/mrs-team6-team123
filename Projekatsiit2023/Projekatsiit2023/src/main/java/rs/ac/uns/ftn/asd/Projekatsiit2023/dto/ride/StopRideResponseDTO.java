package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class StopRideResponseDTO {
    private Long rideId;
    private String stoppedLocation;
    private LocalDateTime stoppedAt;
    private double recalculatedPrice;
    private String message;

    public StopRideResponseDTO() {
    }

}