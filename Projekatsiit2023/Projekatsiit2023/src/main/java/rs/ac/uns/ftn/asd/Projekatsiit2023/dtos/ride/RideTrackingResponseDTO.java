package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RideTrackingResponseDTO {
    private Long rideId;
    private Long driverId;
    private List<GeoPointDTO> stops = new ArrayList<>();
    private int stopsMade = 0;
    private RideInfoDTO info = new RideInfoDTO();
}
