// PanicAlertRequestDTO.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PanicAlertRequestDTO {
    private Long rideId;
    private Long userId;
    private String currentLocation;
}