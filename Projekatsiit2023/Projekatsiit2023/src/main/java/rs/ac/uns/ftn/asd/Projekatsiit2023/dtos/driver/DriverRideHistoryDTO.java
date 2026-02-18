package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;

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
    private List<String> passengers = new ArrayList<>();
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalTime endedAt;
    private LocalDate date;
    private double price;
    private String panicTriggered;
    private String canceledBy;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private List<String> reports = new ArrayList<>();
    private List<GeoPointDTO> stops = new ArrayList<>();
}
