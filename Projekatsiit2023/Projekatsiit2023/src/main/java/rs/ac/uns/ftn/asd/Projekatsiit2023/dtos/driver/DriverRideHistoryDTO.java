package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DriverRideHistoryDTO {
    private Long rideId;
    private List<Long> passengerIds = new ArrayList<>();
    private String startLocation;
    private String endLocation;
    private LocalTime startedAt;
    private LocalTime endedAt;
    private LocalDate date;
    private double price;
    private boolean panicTriggered;
    private Long canceledByUserId;
    private Long routeId;
}
