package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RideOrderRequestDTO {
    private Long creatorId;
    private List<Long> passengerIds;
    private String startLocation;
    private String endLocation;
    private LocalDateTime scheduledAt; // null for ASAP
    private boolean babySeat;
    private boolean petFriendly;
    private String vehicleType; // STANDARD | LUX | VAN
    private List<String> waypoints; // ordered intermediate stops

    public RideOrderRequestDTO() {
    }

}
