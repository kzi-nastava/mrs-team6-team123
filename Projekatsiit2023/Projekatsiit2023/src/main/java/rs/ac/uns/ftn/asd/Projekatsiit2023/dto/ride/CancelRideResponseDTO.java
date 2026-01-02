package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelRideResponseDTO {
    private Long rideId;
    private Long cancelledBy;
    private String reason;
    private String message;

    public CancelRideResponseDTO() {
    }

}