package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

@Setter
@Getter
public class RideResponseDTO {
    private Long rideId;
    private Long driverId;
    private String driverName;
    private String vehicleLicense;
    private RideStatus status; // CREATED, STARTED, FINISHED
    private Integer estimatedTimeMinutes;
    private Double estimatedPrice;

    public RideResponseDTO() {
    }

}
