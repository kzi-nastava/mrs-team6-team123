package rs.ac.uns.ftn.asd.Projekatsiit2023.dto;

public class RideTrackingResponseDTO {
    private Long rideId;
    private String currentLocation;
    private String nextStop;
    private int timeLeft;

    public RideTrackingResponseDTO() {}

    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    public String getCurrentLocation() {
        return currentLocation;
    }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    public String getNextStop() {
        return nextStop;
    }
    public void setNextStop(String nextStop) {
        this.nextStop = nextStop;
    }
    public int getTimeLeft() {
        return timeLeft;
    }
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
