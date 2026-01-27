// DriverStatusResponseDTO.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DriverStatusResponseDTO {
    private Long driverId;
    private boolean active;
    private boolean hasActiveRide;
    private String message;
}