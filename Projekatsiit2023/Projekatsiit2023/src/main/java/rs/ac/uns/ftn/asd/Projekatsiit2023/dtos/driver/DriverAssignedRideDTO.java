package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignedRideDTO {
    private Long rideId;
    private String startLocation;
    private String endLocation;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private RideStatus status;
    private LocalDateTime scheduledAt;
    private double estimatedPrice;
    private List<String> passengerNames;
    private String vehicleType;
}
