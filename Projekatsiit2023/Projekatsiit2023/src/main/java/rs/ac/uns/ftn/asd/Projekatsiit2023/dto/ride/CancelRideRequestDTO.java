package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelRideRequestDTO {
    private Long userId;
    private String reason;

    public CancelRideRequestDTO() {
    }

}