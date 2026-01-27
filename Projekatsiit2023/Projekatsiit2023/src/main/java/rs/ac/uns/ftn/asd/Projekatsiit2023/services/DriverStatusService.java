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

    public boolean hasActiveRide(Long driverId) {
        List<Ride> driverRides = rideRepository.findByDriverId(driverId);
        
        return driverRides.stream()
                .anyMatch(ride -> ride.getStatus() == RideStatus.CREATED 
                        || ride.getStatus() == RideStatus.ACCEPTED 
                        || ride.getStatus() == RideStatus.STARTED);
    }


    public Ride getActiveRide(Long driverId) {
        List<Ride> driverRides = rideRepository.findByDriverId(driverId);
        
        return driverRides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.CREATED 
                        || ride.getStatus() == RideStatus.ACCEPTED 
                        || ride.getStatus() == RideStatus.STARTED)
                .findFirst()
                .orElse(null);
    }


    @Transactional
    public void activateDriverOnLogin(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        driver.setActive(true);
        driverRepository.save(driver);
    }


    @Transactional
    public DriverStatusResponseDTO changeDriverStatus(Long driverId, boolean wantsToBeActive) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverStatusResponseDTO response = new DriverStatusResponseDTO();
        response.setDriverId(driverId);

        boolean hasActiveRide = hasActiveRide(driverId);
        response.setHasActiveRide(hasActiveRide);

        if (wantsToBeActive) {
            driver.setActive(true);
            driverRepository.save(driver);
            response.setActive(true);
            response.setMessage("You are now active and available for rides.");
        } else {
            if (hasActiveRide) {
                driver.setActiveMinutesLast24h(-1);
                driverRepository.save(driver);
                response.setActive(true);
                response.setMessage("You have an active ride. You will become inactive after the ride is finished.");
            } else {
                driver.setActive(false);
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

    @Transactional
    public void checkPendingDeactivation(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getActiveMinutesLast24h() == -1) {
            driver.setActive(false);
            driver.setActiveMinutesLast24h(0);
            driverRepository.save(driver);
        }
    }

    public boolean canLogout(Long driverId) {
        return !hasActiveRide(driverId);
    }


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

    public boolean isAvailableForNewRide(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!driver.isActive()) {
            return false;
        }

        if (driver.getActiveMinutesLast24h() == -1) {
            return false;
        }

        if (driver.getActiveMinutesLast24h() >= 480) {
            return false;
        }

        if (hasActiveRide(driverId)) {
            return false;
        }

        return true;
    }
}