package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

public class RideRatingRequestDTO {
    private Long rideId;
    private int driverRating;
    private int vehicleRating;

    public RideRatingRequestDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(int vehicleRating) {
        this.vehicleRating = vehicleRating;
    }
}
