package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RideOrderRequestDTO {
    private Long creatorId;
    private List<Long> passengerIds;
    private String startLocation;
    private String endLocation;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private LocalDateTime scheduledAt; // null for ASAP
    private boolean babySeat;
    private boolean petFriendly;
    private VehicleType vehicleType; // STANDARD | LUXURY | VAN
    private List<String> waypoints; // ordered intermediate stops
    private Double estimatedPrice; // price calculated on frontend

    public RideOrderRequestDTO() {
    }

}
