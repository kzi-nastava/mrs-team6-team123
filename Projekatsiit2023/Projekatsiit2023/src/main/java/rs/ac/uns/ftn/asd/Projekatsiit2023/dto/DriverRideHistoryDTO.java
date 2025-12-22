package rs.ac.uns.ftn.asd.Projekatsiit2023.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DriverRideHistoryDTO {
    private Long rideId;
    private List<Long> passengerIds;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private double price;
    private boolean panicTriggered;
    private Long canceledByUserId;

    public DriverRideHistoryDTO() {}

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<Long> getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(List<Long> passengerIds) {
        this.passengerIds = passengerIds;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(boolean panicTriggered) {
        this.panicTriggered = panicTriggered;
    }

    public Long getCanceledByUserId() {
        return canceledByUserId;
    }

    public void setCanceledByUserId(Long canceledByUserId) {
        this.canceledByUserId = canceledByUserId;
    }
}
