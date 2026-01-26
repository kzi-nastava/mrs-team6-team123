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
        // Pronađi vožnju
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Pronađi korisnika koji otkazuje
        User cancellingUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validacija razloga
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new RuntimeException("Cancellation reason is required");
        }

        // Proveri da li je vožnja već otkazana
        if (ride.getCanceledBy() != null) {
            throw new RuntimeException("Ride already cancelled");
        }

        // Proveri da li je vožnja već počela
        if (ride.getStatus() == RideStatus.STARTED) {
            throw new RuntimeException("Cannot cancel ride that has already started");
        }

        // Proveri da li je vožnja već završena
        if (ride.getStatus() == RideStatus.FINISHED) {
            throw new RuntimeException("Cannot cancel finished ride");
        }

        // Validacija prema tipu korisnika
        validateCancellation(ride, cancellingUser);

        // Otkaži vožnju
        ride.setCanceledBy(cancellingUser);
        ride.setStatus(RideStatus.FINISHED); // Možeš dodati CANCELLED status ako želiš
        rideRepository.save(ride);

        // Pošalji notifikacije svim učesnicima
        sendCancellationNotifications(ride, cancellingUser, request.getReason());

        // Kreiraj response
        CancelRideResponseDTO response = new CancelRideResponseDTO();
        response.setRideId(rideId);
        response.setCancelledBy(cancellingUser.getId());
        response.setReason(request.getReason());
        response.setMessage("Ride successfully cancelled.");

        return response;
    }

    private void validateCancellation(Ride ride, User cancellingUser) {
        UserRole role = cancellingUser.getUserRole();

        // Vozač može otkazati bilo kada (pre početka)
        if (role == UserRole.DRIVER) {
            if (!ride.getDriver().getId().equals(cancellingUser.getId())) {
                throw new RuntimeException("Only assigned driver can cancel this ride");
            }
            return; // Vozač može otkazati uvek
        }

        // Putnik može otkazati samo 10+ minuta pre početka
        if (role == UserRole.PASSENGER) {
            // Proveri da li je korisnik učesnik vožnje
            boolean isParticipant = ride.getCreator().getId().equals(cancellingUser.getId()) ||
                    ride.getPassengers().stream()
                            .anyMatch(p -> p.getId().equals(cancellingUser.getId()));

            if (!isParticipant) {
                throw new RuntimeException("User is not part of this ride");
            }

            // Proveri vreme do početka vožnje
            // Pretpostavljamo da je scheduledAt LocalDateTime
            // Ako nemaš scheduledAt, možeš koristiti createdAt ili neki drugi timestamp
            LocalDateTime now = LocalDateTime.now();
            
            // TODO: Dodaj scheduledAt u Ride model ako ga nemaš
            // Za sada koristimo startedAt (ili dodaj scheduledAt u modelu)
            // LocalDateTime scheduledTime = ride.getScheduledAt();
            
            // PRIVREMENO: pretpostavljamo da je vožnja zakazana za sada + 5 min
            // U pravoj implementaciji treba scheduledAt polje
            LocalDateTime scheduledTime = now.plusMinutes(5); // OVDE PROMENI SA PRAVIM POLJEM!

            long minutesUntilRide = ChronoUnit.MINUTES.between(now, scheduledTime);

            if (minutesUntilRide < 10) {
                throw new RuntimeException(
                    "Passengers can only cancel rides 10 or more minutes before scheduled start. " +
                    "Time remaining: " + minutesUntilRide + " minutes"
                );
            }
            return;
        }

        throw new RuntimeException("Invalid user role for cancellation");
    }

    private void sendCancellationNotifications(Ride ride, User cancellingUser, String reason) {
        String cancellerName = cancellingUser.getFirstName() + " " + cancellingUser.getLastName();
        String message = "Ride cancelled by " + cancellerName + ". Reason: " + reason;

        // Notifikuj vozača (ako nije on otkazao)
        if (!ride.getDriver().getId().equals(cancellingUser.getId())) {
            notificationService.sendNotification(
                ride.getDriver().getId(),
                "Ride Cancelled",
                message
            );
        }

        // Notifikuj kreatora (ako nije on otkazao)
        if (!ride.getCreator().getId().equals(cancellingUser.getId())) {
            notificationService.sendNotification(
                ride.getCreator().getId(),
                "Ride Cancelled",
                message
            );
        }

        // Notifikuj sve putnike (osim onoga ko je otkazao)
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