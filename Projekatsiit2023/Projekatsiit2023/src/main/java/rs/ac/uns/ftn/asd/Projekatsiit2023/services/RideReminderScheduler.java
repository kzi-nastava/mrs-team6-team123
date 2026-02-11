package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RideReminderScheduler {

    private final RideRepository rideRepository;
    private final NotificationService notificationService;

    public RideReminderScheduler(RideRepository rideRepository, NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.notificationService = notificationService;
    }

    // Run every minute
    @Scheduled(fixedRate = 60000)
    public void checkUpcomingRides() {
        List<Ride> scheduledRides = rideRepository.findScheduledRides();
        LocalDateTime now = LocalDateTime.now();

        for (Ride ride : scheduledRides) {
            LocalDateTime scheduledTime = ride.getScheduledAt();
            long minutesUntilRide = ChronoUnit.MINUTES.between(now, scheduledTime);

            // Send 15-minute reminder
            if (!ride.isNotified15Min() && minutesUntilRide <= 15 && minutesUntilRide > 10) {
                sendReminderToPassengers(ride, 15);
                ride.setNotified15Min(true);
                rideRepository.save(ride);
            }

            // Send 10-minute reminder
            if (!ride.isNotified10Min() && minutesUntilRide <= 10 && minutesUntilRide > 5) {
                sendReminderToPassengers(ride, 10);
                ride.setNotified10Min(true);
                rideRepository.save(ride);
            }

            // Send 5-minute reminder
            if (!ride.isNotified5Min() && minutesUntilRide <= 5 && minutesUntilRide > 0) {
                sendReminderToPassengers(ride, 5);
                ride.setNotified5Min(true);
                rideRepository.save(ride);
            }
        }
    }

    private void sendReminderToPassengers(Ride ride, int minutesBefore) {
        String title = "Ride Reminder";
        String message = String.format(
                "Your ride from %s to %s starts in %d minutes!",
                ride.getStartLocation(),
                ride.getEndLocation(),
                minutesBefore);

        // Send to creator
        notificationService.sendNotification(
                ride.getCreator().getId(),
                title,
                message,
                "/ride-history");

        // Send to all passengers (skip creator to avoid duplicate)
        for (Passenger passenger : ride.getPassengers()) {
            if (!passenger.getId().equals(ride.getCreator().getId())) {
                notificationService.sendNotification(
                        passenger.getId(),
                        title,
                        message,
                        "/ride-history");
            }
        }
    }
}
