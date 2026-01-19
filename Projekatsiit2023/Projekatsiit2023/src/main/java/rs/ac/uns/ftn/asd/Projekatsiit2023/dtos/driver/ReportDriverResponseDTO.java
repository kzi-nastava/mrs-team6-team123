package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDriverResponseDTO {
    private Long rideId;
    private Long driverId;
    private Long vehicleId;
    private String comment;

    public ReportDriverResponseDTO() {
    }
}
