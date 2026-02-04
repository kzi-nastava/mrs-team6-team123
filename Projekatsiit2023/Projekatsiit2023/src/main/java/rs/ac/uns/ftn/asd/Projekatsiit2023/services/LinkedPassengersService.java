package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;

@Service
public class LinkedPassengersService {
    private final EmailService emailService;
    private final NotificationService notificationService;

    public LinkedPassengersService(
            EmailService emailService,
            NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // TODO: use these methods at start ride
    public void sendEmail(Passenger passenger, Ride ride) {
        String to = passenger.getEmail();
        String rideDetails = getRideDetails(ride);
        String trackRideLink = "http://localhost:4200/track-ride-page?rideId=" + ride.getId();
        emailService.sendLinkedPassengersEmail(to, rideDetails, trackRideLink);
    }

    public void sendNotification(Passenger passenger, Ride ride) {
        String title = "Ride Started - Track Your Ride!";
        String rideDetails = getRideDetails(ride);
        String trackRideLink = "/track-ride-page?rideId=" + ride.getId();
        String message = String.format(
                "Hello,\n\n" +
                        "Your ride has started. Here are the details of your ride:\n\n" +
                        "%s\n\n" +
                        "You can track your ride in real-time.\n\n" +
                        "Thank you for choosing our taxi service!\n\n",
                rideDetails);
        notificationService.sendNotification(passenger.getId(), title, message, trackRideLink);
    }

    private String getRideDetails(Ride ride) {
        return ride.getStartLocation() + " -> " + ride.getEndLocation() +
                "\nDate: " + ride.getDate() +
                "\nStarted at: " + ride.getStartedAt() +
                "\nDriver: " + ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() +
                "\nCreated by: " + ride.getCreator().getFirstName() + " " + ride.getCreator().getLastName() +
                "\nTotal Price: $" + ride.getPrice();
    }
}
