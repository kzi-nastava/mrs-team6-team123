package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RideOrderRequestDTO {
    @NotNull(message = "Creator ID is required")
    @Positive(message = "Creator ID must be positive")
    private Long creatorId;
    @NotNull(message = "Passenger IDs are required")
    private List<Long> passengerIds;
    @NotBlank(message = "Start location is required")
    private String startLocation;
    @NotBlank(message = "End location is required")
    private String endLocation;
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private double startLatitude;
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private double startLongitude;
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private double endLatitude;
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private double endLongitude;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private LocalDateTime scheduledAt; // null for ASAP, ISO format: YYYY-MM-DDTHH:mm:ss
    private boolean babySeat;
    private boolean petFriendly;
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType; // STANDARD | LUXURY | VAN

    private List<String> waypoints; // ordered intermediate stops
    @Positive(message = "Estimated price must be positive")
    private Double estimatedPrice; // price calculated on frontend

    public RideOrderRequestDTO() {
    }

}
