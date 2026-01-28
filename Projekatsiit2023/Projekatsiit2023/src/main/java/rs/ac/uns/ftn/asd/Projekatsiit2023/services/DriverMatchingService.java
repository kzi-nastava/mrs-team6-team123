package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.*;

@Service
public class DriverMatchingService {

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    public DriverMatchingService(DriverRepository driverRepository, RideRepository rideRepository) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
    }

    
    public Optional<Driver> findBestDriver(VehicleType vehicleType, boolean babyTransport,
            boolean petTransport, double startLatitude, double startLongitude) {

        // Get all active drivers available for ride (not currently on a STARTED ride)
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(RideStatus.STARTED,
                babyTransport, petTransport);

        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // vehicle type
        availableDrivers = availableDrivers.stream()
                .filter(d -> d.getVehicle() != null && d.getVehicle().getVehicleType() == vehicleType)
                .toList();

        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        //  <= 8 hours in last 24h
        List<Driver> eligibleDrivers = availableDrivers.stream()
                .filter(d -> d.getActiveMinutesLast24h() <= 480)
                .toList();

        if (eligibleDrivers.isEmpty()) {
            return Optional.empty();
        }

        // not on ACCEPTED or STARTED rides
        List<Driver> freeDrivers = new ArrayList<>();

        for (Driver driver : eligibleDrivers) {
            List<Ride> activeRides = rideRepository.findByDriverId(driver.getId()).stream()
                    .filter(r -> r.getStatus() == RideStatus.ACCEPTED || r.getStatus() == RideStatus.STARTED)
                    .toList();

            if (activeRides.isEmpty()) {
                freeDrivers.add(driver);
            }
        }

        // If no free drivers, return empty (ride queues or is rejected)
        if (freeDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Return any free driver
        return freeDrivers.stream().findFirst();
    }
}
