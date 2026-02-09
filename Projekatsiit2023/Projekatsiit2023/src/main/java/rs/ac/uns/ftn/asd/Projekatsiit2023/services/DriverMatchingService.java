package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;

import java.util.*;

@Service
public class DriverMatchingService {

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final RideEstimationService estimationService;

    public DriverMatchingService(DriverRepository driverRepository, RideRepository rideRepository,
            RideEstimationService estimationService) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.estimationService = estimationService;
    }

    public Optional<Driver> findBestDriver(VehicleType vehicleType, boolean babyTransport,
            boolean petTransport, double startLatitude, double startLongitude,
            double endLatitude, double endLongitude) {

        // Get all active drivers available for ride (not currently on a STARTED ride)
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(RideStatus.STARTED,
                babyTransport, petTransport);

        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Filter by active status (driver must be active to receive rides)
        availableDrivers = availableDrivers.stream()
                .filter(Driver::isActive)
                .toList();

        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Filter by vehicle type
        availableDrivers = availableDrivers.stream()
                .filter(d -> d.getVehicle() != null && d.getVehicle().getVehicleType() == vehicleType)
                .toList();

        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Filter by work hour limit with consideration for upcoming ride duration
        // Estimate the ride time for this request
        int estimatedRideTimeMinutes = estimateRideTime(startLatitude, startLongitude,
                endLatitude, endLongitude, vehicleType);

        List<Driver> eligibleDrivers = availableDrivers.stream()
                .filter(d -> {
                    int currentActiveMinutes = d.getActiveMinutesLast24h();
                    int projectedTotalMinutes = currentActiveMinutes + estimatedRideTimeMinutes;
                    // Driver can take the ride only if total time stays under 8 hours (480 minutes)
                    return projectedTotalMinutes <= 480;
                })
                .toList();

        if (eligibleDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Priority-based driver selection
        // Priority 1: Drivers with NO active rides (neither CREATED nor STARTED)
        List<Driver> noActiveRidesDrivers = new ArrayList<>();
        // Priority 2: Drivers with only STARTED rides (no CREATED rides - assigned but not started)
        List<Driver> onlyStartedRidesDrivers = new ArrayList<>();

        for (Driver driver : eligibleDrivers) {
            List<Ride> createdRides = rideRepository.findByDriverId(driver.getId()).stream()
                    .filter(r -> r.getStatus() == RideStatus.CREATED)
                    .toList();

            List<Ride> startedRides = rideRepository.findByDriverId(driver.getId()).stream()
                    .filter(r -> r.getStatus() == RideStatus.STARTED)
                    .toList();

            if (createdRides.isEmpty() && startedRides.isEmpty()) {
                // Priority 1: No assigned or active rides
                noActiveRidesDrivers.add(driver);
            } else if (createdRides.isEmpty() && !startedRides.isEmpty()) {
                // Priority 2: Only STARTED rides, no assigned (CREATED) rides
                onlyStartedRidesDrivers.add(driver);
            }
        }

        // Return Priority 1 drivers first (no active rides)
        if (!noActiveRidesDrivers.isEmpty()) {
            return noActiveRidesDrivers.stream().findFirst();
        }

        // Fall back to Priority 2 drivers (only STARTED rides)
        if (!onlyStartedRidesDrivers.isEmpty()) {
            return onlyStartedRidesDrivers.stream().findFirst();
        }

        // No suitable drivers available
        return Optional.empty();
    }

    /**
     * Estimates the ride time in minutes for a given route.
     * Uses the actual start and end coordinates for accurate estimation.
     */
    private int estimateRideTime(double startLatitude, double startLongitude,
            double endLatitude, double endLongitude, VehicleType vehicleType) {
        try {
            RideEstimationRequestDTO estimationRequest = new RideEstimationRequestDTO();
            estimationRequest.setStartLocation(startLatitude + "," + startLongitude);
            estimationRequest.setEndLocation(endLatitude + "," + endLongitude);
            estimationRequest.setVehicleType(vehicleType);

            RideEstimationResponseDTO estimation = estimationService.estimate(estimationRequest);
            return estimation.getEstimatedTime();
        } catch (Exception e) {
            // Fallback to conservative estimate (30 minutes) if estimation fails
            System.err.println("Failed to estimate ride time, using default: " + e.getMessage());
            return 30;
        }
    }
}
