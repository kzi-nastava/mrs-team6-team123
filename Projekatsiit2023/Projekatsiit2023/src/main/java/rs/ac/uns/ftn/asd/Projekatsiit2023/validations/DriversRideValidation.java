package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.Optional;

@Component
public class DriversRideValidation {

    private final RideRepository rideRepository;

    public DriversRideValidation(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public Ride validateRideExists(Long rideId) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty()) {
            throw new RuntimeException("Ride not found");
        }
        return ride.get();
    }

    public void validateRideAssignedToDriver(Long driverId, Ride ride) {
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("This ride is not assigned to you");
        }
    }

    public void validateRideStatusForStart(Ride ride) {
        // Ride must be in CREATED status to start
        if (ride.getStatus() != RideStatus.CREATED) {
            throw new RuntimeException("Ride must be in assigned state to start");
        }
    }
}
