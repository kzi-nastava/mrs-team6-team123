package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import java.time.LocalDateTime;

public class StopRideResponseDTO {
    private Long rideId;
    private String stoppedLocation;
    private LocalDateTime stoppedAt;
    private double recalculatedPrice;
    private String message;

    public StopRideResponseDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStoppedLocation() {
        return stoppedLocation;
    }

    public void setStoppedLocation(String stoppedLocation) {
        this.stoppedLocation = stoppedLocation;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(LocalDateTime stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public double getRecalculatedPrice() {
        return recalculatedPrice;
    }

    public void setRecalculatedPrice(double recalculatedPrice) {
        this.recalculatedPrice = recalculatedPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}