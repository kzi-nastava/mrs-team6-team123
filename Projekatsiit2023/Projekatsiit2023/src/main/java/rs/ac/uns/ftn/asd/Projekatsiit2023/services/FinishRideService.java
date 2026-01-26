package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

@Service
public class FinishRideService {
    private final RideRepository rideRepository;

    public FinishRideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public void finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + rideId));
        if (ride.getStatus() == RideStatus.STARTED)
            ride.setStatus(RideStatus.FINISHED);
        else
            throw new IllegalStateException("Ride cannot be finished from status: " + ride.getStatus());
        rideRepository.save(ride);
    }
}
