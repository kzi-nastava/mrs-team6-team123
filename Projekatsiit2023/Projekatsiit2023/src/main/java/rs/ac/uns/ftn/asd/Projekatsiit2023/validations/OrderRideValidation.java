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

        // Check if passengers are on active rides
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
