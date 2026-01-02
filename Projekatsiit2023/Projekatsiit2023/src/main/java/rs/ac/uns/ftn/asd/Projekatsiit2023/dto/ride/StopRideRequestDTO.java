package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

import java.time.LocalDateTime;

public class StopRideRequestDTO {
    private String currentLocation;
    private LocalDateTime stoppedAt;

    public StopRideRequestDTO() {
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(LocalDateTime stoppedAt) {
        this.stoppedAt = stoppedAt;
    }
}