package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PanicAlertResponseDTO {
    private Long id;
    private Long rideId;
    private Long triggeredByUserId;
    private String triggeredByName;
    private String currentLocation;
    private LocalDateTime triggeredAt;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private Long resolvedByAdminId;
    private String resolutionNotes;
    
    // Info o vožnji
    private Long driverId;
    private String driverName;
    private String startLocation;
    private String endLocation;
}