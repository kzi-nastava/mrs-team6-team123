package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.driver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDriverRequestDTO {
    private Long rideId;
    private Long driverId;
    private String comment;

    public ReportDriverRequestDTO() {
    }
}
