package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DriverRideHistoryDTO {
    private Long rideId;
    private List<Long> passengerIds;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime date;
    private double price;
    private boolean panicTriggered;
    private Long canceledByUserId;
    private Long routeId;
}
