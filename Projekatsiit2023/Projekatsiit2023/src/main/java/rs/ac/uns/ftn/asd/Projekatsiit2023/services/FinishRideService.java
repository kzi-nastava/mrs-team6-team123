package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActiveVehicleRepository;
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
    private final ActiveVehicleRepository activeVehicleRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public FinishRideService(
            RideRepository rideRepository,
            PassengerRepository passengerRepository,
            DriverRepository driverRepository,
            ActiveVehicleRepository activeVehicleRepository,
            EmailService emailService,
            NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.activeVehicleRepository = activeVehicleRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Transactional
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
            ActiveVehicle vehicle =
                    activeVehicleRepository.findByCurrentRideId(rideId).orElse(null);
            if (vehicle != null) {
                vehicle.setCurrentRide(null);
                vehicle.setAvailable(true);
                activeVehicleRepository.save(vehicle);
            }
            if (isDriverAvailable(ride.getDriver().getId()))
                ride.getDriver().setActive(true);
            driverRepository.save(ride.getDriver());
            for (var passenger : ride.getPassengers()) {
                passenger.setStartedRide(false);
                passengerRepository.save(passenger);
                sendEmail(passenger, ride);
                sendNotification(passenger, ride);
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
        String rideDetails = getRideDetails(ride);
        String rateLink = getRateRideLink(ride);
        emailService.sendRideFinishedEmail(to, rideDetails, rateLink);
    }

    private void sendNotification(Passenger passenger, Ride ride) {
        String title = "Your Ride is Complete!";
        String rideDetails = getRideDetails(ride);
        String rateLink = getRateRideLink(ride);
        String message = String.format(
                "Hello,\n\n" +
                        "Your ride has been completed. Here are the details of your ride:\n\n" +
                        "%s\n\n" +
                        "You can now rate the driver and vehicle, and provide feedback on you experience\n\n" +
                        "%s\n\n" +
                        "Thank you for choosing our taxi service!\n\n",
                rideDetails,
                rateLink);
        notificationService.sendNotification(passenger.getId(), title, message);
    }

    private String getRideDetails(Ride ride) {
        return ride.getStartLocation() + " -> " + ride.getEndLocation() +
                "\nDate: " + ride.getDate() +
                "\nTime: " + ride.getStartedAt() + " - " + ride.getEndedAt() +
                "\nDriver: " + ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() +
                "\nTotal Price: $" + ride.getPrice();
    }

    private String getRateRideLink(Ride ride) {
        return "http://localhost:4200/rate-ride?rideId=" + ride.getId();
    }
}
