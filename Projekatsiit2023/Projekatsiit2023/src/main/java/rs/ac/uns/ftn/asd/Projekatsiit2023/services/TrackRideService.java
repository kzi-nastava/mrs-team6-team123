package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideTrackingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.RouteStop;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.Optional;

@Service
public class TrackRideService {
    private final RideRepository repository;

    public TrackRideService(RideRepository repository) {
        this.repository = repository;
    }

    public RideTrackingResponseDTO trackRide(Long rideId) throws RuntimeException {
        Optional<Ride> ride = repository.findById(rideId);
        if (ride.isEmpty()) {
            throw new RuntimeException("Ride not found");
        }
        RideTrackingResponseDTO dto = mapRideToRideTrackingDTO(ride);
        return dto;
    }

    public RideTrackingResponseDTO mapRideToRideTrackingDTO(Optional<Ride> ride) {
        RideTrackingResponseDTO dto = new RideTrackingResponseDTO();
        // TODO: current coordinates, time left
        dto.setRideId(ride.get().getId());
        dto.setDriver(ride.get().getDriver().getFirstName() + " " + ride.get().getDriver().getLastName());
        dto.setStartedAt(ride.get().getStartedAt().toString());
        dto.setFrom(ride.get().getRoute().getStartLocation());
        dto.setTo(ride.get().getRoute().getEndLocation());
        dto.setPrice(ride.get().getPrice());
        for (var passenger : ride.get().getPassengers()) {
            dto.getPassengers().add(passenger.getFirstName() + " " + passenger.getLastName());
        }
        RouteStop nextStop = findNextStop(ride);
        if (nextStop == null) {
            throw new RuntimeException("Ride has already ended");
        }
        dto.setNextStop(nextStop.getLocation());
        dto.setNextStopLatitude(nextStop.getLatitude());
        dto.setNextStopLongitude(nextStop.getLongitude());
        return dto;
    }

    private RouteStop findNextStop(Optional<Ride> ride) {
        int stopsMade = ride.get().getStopsMade();
        if (stopsMade == ride.get().getRoute().getStops().size()) {
            return null;
        }
        return ride.get().getRoute().getStops().get(stopsMade);
    }
}
