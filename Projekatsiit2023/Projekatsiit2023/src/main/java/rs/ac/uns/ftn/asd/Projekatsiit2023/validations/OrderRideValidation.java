package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideOrderRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;

import java.util.List;

@Component
public class OrderRideValidation {

    private final PassengerRepository passengerRepository;

    public OrderRideValidation(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public void validateOrderRideRequest(RideOrderRequestDTO request) {
        // Validate creator ID
        if (request.getCreatorId() == null || request.getCreatorId() <= 0) {
            throw new RuntimeException("Invalid creator ID");
        }

        // Validate locations
        if (request.getStartLocation() == null || request.getStartLocation().trim().isEmpty()) {
            throw new RuntimeException("Start location is required");
        }
        if (request.getEndLocation() == null || request.getEndLocation().trim().isEmpty()) {
            throw new RuntimeException("End location is required");
        }

        // Validate coordinates
        if (request.getStartLatitude() < -90 || request.getStartLatitude() > 90) {
            throw new RuntimeException("Invalid start latitude");
        }
        if (request.getStartLongitude() < -180 || request.getStartLongitude() > 180) {
            throw new RuntimeException("Invalid start longitude");
        }
        if (request.getEndLatitude() < -90 || request.getEndLatitude() > 90) {
            throw new RuntimeException("Invalid end latitude");
        }
        if (request.getEndLongitude() < -180 || request.getEndLongitude() > 180) {
            throw new RuntimeException("Invalid end longitude");
        }

        // Validate vehicle type
        if (request.getVehicleType() == null) {
            throw new RuntimeException("Vehicle type is required");
        }

        // Check if passengers are on active rides (if passengers list is provided)
        if (request.getPassengerIds() != null && !request.getPassengerIds().isEmpty()) {
            List<Passenger> passengers = passengerRepository.findAllById(request.getPassengerIds());
            for (Passenger passenger : passengers) {
                if (passenger.isStartedRide()) {
                    throw new RuntimeException("Passenger " + passenger.getFirstName() + " " + passenger.getLastName() +
                            " is currently on an active ride and cannot order a new one.");
                }
            }
        }
    }
}
