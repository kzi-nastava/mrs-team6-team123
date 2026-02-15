package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PassengerRideHistoryDTO {
    private Long rideId;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalTime endedAt;
    private LocalDate date;
    private double price;

    // Za mapu
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;

    // Podaci o vozaču
    private Long driverId;
    private String driverName;
    private String driverPhoto;
    private double driverRating;

    // Ocene
    private double rideDriverRating;
    private double rideVehicleRating;
    private boolean rated;

    // Prijave nekonzistentnosti
    private List<String> inconsistencyReports = new ArrayList<>();

    // Za ponovo poručivanje
    private Long routeId;
}