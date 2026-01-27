package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalTime;
import java.util.List;

@Service
public class FinishRideService {
    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final EmailService emailService;

    public FinishRideService(
            RideRepository rideRepository,
            PassengerRepository passengerRepository,
            DriverRepository driverRepository,
            EmailService emailService) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.emailService = emailService;
    }

    public void finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + rideId));
        if (ride.getStatus() == RideStatus.STARTED) {
            ride.setStatus(RideStatus.FINISHED);
            ride.setPaid(true);
            ride.setEndLocation(ride.getRoute().getEndLocation());
            ride.setEndLatitude(ride.getRoute().getEndLatitude());
            ride.setEndLongitude(ride.getRoute().getEndLongitude());
            ride.setEndedAt(LocalTime.now());
            if (isDriverAvailable(ride.getDriver().getId()))
                ride.getDriver().setActive(true);
            driverRepository.save(ride.getDriver());
            for (var passenger : ride.getPassengers()) {
                passenger.setStartedRide(false);
                passengerRepository.save(passenger);
                sendEmail(passenger, ride);
            }
            rideRepository.save(ride);
        }
        else
            throw new IllegalStateException("Ride cannot be finished from status: " + ride.getStatus());
    }

    private boolean isDriverAvailable(Long driverId) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        for (Ride ride : rides) {
            if (ride.getStatus() == RideStatus.ACCEPTED) {
                return false;
            }
        }
        return true;
    }

    private void sendEmail(Passenger passenger, Ride ride) {
        String to = passenger.getEmail();
        String rideDetails = ride.getStartLocation() + " -> " + ride.getEndLocation() +
                "\nDate: " + ride.getDate() +
                "\nTime: " + ride.getStartedAt() + " - " + ride.getEndedAt() +
                "\nDriver: " + ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() +
                "\nTotal Price: $" + ride.getPrice();
        String rateLink = "http://localhost:8080/rate-ride?rideId=" + ride.getId();
        emailService.sendRideFinishedEmail(to, rideDetails, rateLink);
    }
}
