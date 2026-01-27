// DriverStatusService.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverStatusResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.List;

@Service
public class DriverStatusService {

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    public DriverStatusService(DriverRepository driverRepository, RideRepository rideRepository) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
    }

    /**
     * Proverava da li vozač ima aktivnu vožnju (CREATED, ACCEPTED, ili STARTED)
     */
    public boolean hasActiveRide(Long driverId) {
        List<Ride> driverRides = rideRepository.findByDriverId(driverId);
        
        return driverRides.stream()
                .anyMatch(ride -> ride.getStatus() == RideStatus.CREATED 
                        || ride.getStatus() == RideStatus.ACCEPTED 
                        || ride.getStatus() == RideStatus.STARTED);
    }

    /**
     * Vraća aktivnu vožnju vozača ako postoji
     */
    public Ride getActiveRide(Long driverId) {
        List<Ride> driverRides = rideRepository.findByDriverId(driverId);
        
        return driverRides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.CREATED 
                        || ride.getStatus() == RideStatus.ACCEPTED 
                        || ride.getStatus() == RideStatus.STARTED)
                .findFirst()
                .orElse(null);
    }

    /**
     * Postavlja vozača na aktivan status prilikom logina
     */
    @Transactional
    public void activateDriverOnLogin(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        driver.setActive(true);
        driverRepository.save(driver);
    }

    /**
     * Menja status vozača (aktivan/neaktivan)
     * Ako vozač ima aktivnu vožnju i želi da postane neaktivan,
     * ostaće aktivan do kraja vožnje (sistem to pamti kroz active=true ali ga ne dodeljuje novim vožnjama)
     */
    @Transactional
    public DriverStatusResponseDTO changeDriverStatus(Long driverId, boolean wantsToBeActive) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverStatusResponseDTO response = new DriverStatusResponseDTO();
        response.setDriverId(driverId);

        boolean hasActiveRide = hasActiveRide(driverId);
        response.setHasActiveRide(hasActiveRide);

        if (wantsToBeActive) {
            // Vozač želi da postane aktivan
            driver.setActive(true);
            driverRepository.save(driver);
            response.setActive(true);
            response.setMessage("You are now active and available for rides.");
        } else {
            // Vozač želi da postane neaktivan
            if (hasActiveRide) {
                // Ima aktivnu vožnju - ostaje aktivan ali ga sistem neće dodeljivati novim vožnjama
                // Koristimo activeMinutesLast24h kao flag: -1 znači "pending deactivation"
                driver.setActiveMinutesLast24h(-1);
                driverRepository.save(driver);
                response.setActive(true); // još uvek je tehnički aktivan
                response.setMessage("You have an active ride. You will become inactive after the ride is finished.");
            } else {
                // Nema aktivnu vožnju - može odmah postati neaktivan
                driver.setActive(false);
                // Resetuj flag ako je bio postavljen
                if (driver.getActiveMinutesLast24h() < 0) {
                    driver.setActiveMinutesLast24h(0);
                }
                driverRepository.save(driver);
                response.setActive(false);
                response.setMessage("You are now inactive and will not receive new rides.");
            }
        }

        return response;
    }

    /**
     * Proverava da li vozač čeka deaktivaciju (pozvati nakon završetka vožnje)
     */
    @Transactional
    public void checkPendingDeactivation(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Ako je activeMinutesLast24h == -1, vozač je tražio deaktivaciju tokom vožnje
        if (driver.getActiveMinutesLast24h() == -1) {
            driver.setActive(false);
            driver.setActiveMinutesLast24h(0); // resetuj flag
            driverRepository.save(driver);
        }
    }

    /**
     * Proverava da li vozač može da se odjavi
     */
    public boolean canLogout(Long driverId) {
        return !hasActiveRide(driverId);
    }

    /**
     * Vraća trenutni status vozača
     */
    public DriverStatusResponseDTO getDriverStatus(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverStatusResponseDTO response = new DriverStatusResponseDTO();
        response.setDriverId(driverId);
        response.setActive(driver.isActive());
        response.setHasActiveRide(hasActiveRide(driverId));
        
        if (driver.getActiveMinutesLast24h() == -1) {
            response.setMessage("Pending deactivation after current ride.");
        } else if (driver.isActive()) {
            response.setMessage("Active and available for rides.");
        } else {
            response.setMessage("Inactive.");
        }

        return response;
    }

    /**
     * Proverava da li vozač može biti dodeljen novoj vožnji
     * (aktivan, nema pending deactivation, nema 8+ sati rada)
     */
    public boolean isAvailableForNewRide(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Nije aktivan
        if (!driver.isActive()) {
            return false;
        }

        // Čeka deaktivaciju
        if (driver.getActiveMinutesLast24h() == -1) {
            return false;
        }

        // Ima više od 8 sati rada (480 minuta)
        if (driver.getActiveMinutesLast24h() >= 480) {
            return false;
        }

        // Već ima aktivnu vožnju
        if (hasActiveRide(driverId)) {
            return false;
        }

        return true;
    }
}