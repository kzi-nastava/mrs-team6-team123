package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PanicAlert;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PanicAlertRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PanicAlertService {

    private final PanicAlertRepository panicAlertRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PanicAlertService(PanicAlertRepository panicAlertRepository,
                            RideRepository rideRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.panicAlertRepository = panicAlertRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PanicAlert triggerPanic(Long rideId, Long userId, String currentLocation) {
        // Pronađi vožnju
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Pronađi korisnika
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Proveri da li je korisnik učesnik vožnje
        boolean isParticipant = ride.getDriver().getId().equals(userId) ||
                                ride.getCreator().getId().equals(userId) ||
                                ride.getPassengers().stream()
                                    .anyMatch(p -> p.getId().equals(userId));

        if (!isParticipant) {
            throw new RuntimeException("User is not part of this ride");
        }

        // Kreiraj panic alert
        PanicAlert panicAlert = new PanicAlert(ride, user, currentLocation);
        panicAlertRepository.save(panicAlert);

        // Označi vožnju kao panic triggered
        ride.setPanicTriggered(true);
        rideRepository.save(ride);

        // Pošalji notifikacije SVIM adminima
        sendPanicNotificationsToAdmins(panicAlert);

        System.out.println("🚨 PANIC ALERT triggered!");
        System.out.println("   Ride ID: " + rideId);
        System.out.println("   Triggered by: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("   Location: " + currentLocation);

        return panicAlert;
    }

    private void sendPanicNotificationsToAdmins(PanicAlert alert) {
        String userName = alert.getTriggeredBy().getFirstName() + " " + 
                         alert.getTriggeredBy().getLastName();
        
        String message = "🚨 PANIC ALERT! " +
                        "Ride #" + alert.getRide().getId() + " - " +
                        userName + " triggered panic button. " +
                        "Location: " + alert.getCurrentLocation();

        notificationService.sendNotification(null, "PANIC ALERT", message);
    }

    public List<PanicAlert> getAllUnresolvedAlerts() {
        return panicAlertRepository.findByResolvedFalseOrderByTriggeredAtDesc();
    }

    public List<PanicAlert> getAllAlerts() {
        return panicAlertRepository.findAllByOrderByTriggeredAtDesc();
    }

    @Transactional
    public PanicAlert resolveAlert(Long alertId, Long adminId, String notes) {
        PanicAlert alert = panicAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Panic alert not found"));

        if (alert.isResolved()) {
            throw new RuntimeException("Alert already resolved");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(admin);
        alert.setResolutionNotes(notes);

        return panicAlertRepository.save(alert);
    }
}