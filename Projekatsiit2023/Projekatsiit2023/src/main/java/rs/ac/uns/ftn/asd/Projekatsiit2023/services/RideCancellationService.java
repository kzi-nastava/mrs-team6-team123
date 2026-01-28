package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.CancelRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.CancelRideResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RideCancellationService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RideCancellationService(RideRepository rideRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public CancelRideResponseDTO cancelRide(Long rideId, CancelRideRequestDTO request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User cancellingUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new RuntimeException("Cancellation reason is required");
        }

        if (ride.getCanceledBy() != null) {
            throw new RuntimeException("Ride already cancelled");
        }

        if (ride.getStatus() == RideStatus.STARTED) {
            throw new RuntimeException("Cannot cancel ride that has already started");
        }

        if (ride.getStatus() == RideStatus.FINISHED) {
            throw new RuntimeException("Cannot cancel finished ride");
        }

        validateCancellation(ride, cancellingUser);


        ride.setCanceledBy(cancellingUser);
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);

        sendCancellationNotifications(ride, cancellingUser, request.getReason());

        CancelRideResponseDTO response = new CancelRideResponseDTO();
        response.setRideId(rideId);
        response.setCancelledBy(cancellingUser.getId());
        response.setReason(request.getReason());
        response.setMessage("Ride successfully cancelled.");

        return response;
    }

private void validateCancellation(Ride ride, User cancellingUser) {
    UserRole role = cancellingUser.getUserRole();

    if (role == UserRole.DRIVER) {
        if (!ride.getDriver().getId().equals(cancellingUser.getId())) {
            throw new RuntimeException("Only assigned driver can cancel this ride");
        }
        
        if (ride.getStatus() == RideStatus.STARTED) {
            throw new RuntimeException("Cannot cancel ride after passengers have entered the vehicle");
        }
        
        return;
    }

    if (role == UserRole.PASSENGER) {
        boolean isParticipant = ride.getCreator().getId().equals(cancellingUser.getId()) ||
                ride.getPassengers().stream()
                        .anyMatch(p -> p.getId().equals(cancellingUser.getId()));

        if (!isParticipant) {
            throw new RuntimeException("User is not part of this ride");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledTime = ride.getScheduledAt();

        if (scheduledTime == null) {
            throw new RuntimeException(
                "Cannot cancel immediate rides. Only scheduled rides can be cancelled by passengers 10+ minutes before start."
            );
        }

        long minutesUntilRide = java.time.temporal.ChronoUnit.MINUTES.between(now, scheduledTime);

        if (minutesUntilRide < 10) {
            throw new RuntimeException(
                "Passengers can only cancel rides 10 or more minutes before scheduled start. " +
                "Time remaining: " + minutesUntilRide + " minutes."
            );
        }
        
        return; 
    }

    if (role == UserRole.ADMIN) {
        return;
    }

    throw new RuntimeException("Invalid user role for cancellation");
}
    private void sendCancellationNotifications(Ride ride, User cancellingUser, String reason) {
        String cancellerName = cancellingUser.getFirstName() + " " + cancellingUser.getLastName();
        String message = "Ride cancelled by " + cancellerName + ". Reason: " + reason;

        if (!ride.getDriver().getId().equals(cancellingUser.getId())) {
            notificationService.sendNotification(
                ride.getDriver().getId(),
                "Ride Cancelled",
                message
            );
        }

        if (!ride.getCreator().getId().equals(cancellingUser.getId())) {
            notificationService.sendNotification(
                ride.getCreator().getId(),
                "Ride Cancelled",
                message
            );
        }

        ride.getPassengers().stream()
            .filter(p -> !p.getId().equals(cancellingUser.getId()))
            .forEach(passenger -> {
                notificationService.sendNotification(
                    passenger.getId(),
                    "Ride Cancelled",
                    message
                );
            });
    }
}