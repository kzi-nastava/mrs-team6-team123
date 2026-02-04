package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RideMonitoringResponseDTO {
    private Long rideId;
    private Long driverId;
    private String driverName;
    private String licencePlate;
    private String from;
    private String to;
}
