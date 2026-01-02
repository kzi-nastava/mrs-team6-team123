package rs.ac.uns.ftn.asd.Projekatsiit2023.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Ride {
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
    private Long routeId;
    private String status;

    public Ride() {}

}
