package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class StopRideRequestDTO {
    private String currentLocation;
    private LocalDateTime stoppedAt;

    public StopRideRequestDTO() {
    }

}