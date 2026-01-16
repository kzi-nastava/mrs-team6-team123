package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class AdminRideHistoryDTO {
    private Long rideId;
    private Long driverId;
    private Long creatorId;
    private List<Long> passengerIds;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private double price;
    private double totalDistance;
    private boolean panicTriggered;
    private Long canceledByUserId;
    private String cancelReason;
    private String route;
    private List<String> inconsistencyReports;
    private Integer driverRating;
    private Integer vehicleRating;
    private String driverName;
    private VehicleType vehicleType;

    public AdminRideHistoryDTO() {
    }

}