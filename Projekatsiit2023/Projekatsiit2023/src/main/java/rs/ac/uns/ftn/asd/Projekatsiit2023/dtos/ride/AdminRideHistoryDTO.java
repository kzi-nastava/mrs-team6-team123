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
public class AdminRideHistoryDTO {
    private Long rideId;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalTime endedAt;
    private LocalDate date;
    private double price;
    private double totalDistance;

    // Za mapu
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;

    // Vozač
    private Long driverId;
    private String driverName;
    private String driverPhoto;

    // Kreator vožnje
    private Long creatorId;
    private String creatorName;

    // Svi putnici
    private List<PassengerInfoDTO> passengers = new ArrayList<>();

    // Otkazivanje
    private boolean cancelled;
    private Long cancelledByUserId;
    private String cancelledByName;
    private String cancelledByRole;

    // PANIC
    private boolean panicTriggered;

    // Ocene
    private double driverRating;
    private double vehicleRating;
    private boolean rated;

    // Prijave nekonzistentnosti
    private List<String> inconsistencyReports = new ArrayList<>();

    // Za ponovo poručivanje
    private Long routeId;
}