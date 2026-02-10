package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DriverMatchingService {

    private static final int BUFFER_MINUTES = 10;
    private static final int DEFAULT_ESTIMATED_MINUTES = 30;

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
            double endLatitude, double endLongitude, LocalDateTime requestedScheduledAt) {

        // STEP 1: get all active drivers
        List<Driver> availableDrivers = driverRepository.findByActive(true);
        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // STEP 2: get drivers with right vehicle and correct preferences
        availableDrivers = availableDrivers.stream()
                .filter(d -> d.getVehicle() != null && d.getVehicle().getVehicleType() == vehicleType)
                .filter(d -> !babyTransport || d.getVehicle().isBabyTransport())
                .filter(d -> !petTransport || d.getVehicle().isPetTransport())
                .toList();
        if (availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        int estimatedRideTimeMinutes = estimateRideTime(startLatitude, startLongitude,
                endLatitude, endLongitude, vehicleType);

        // STEP 3: make sure driver will work <= 8hrs
        List<Driver> eligibleDrivers = availableDrivers.stream()
                .filter(d -> {
                    int currentActiveMinutes = d.getActiveMinutesLast24h();
                    int projectedTotalMinutes = currentActiveMinutes + estimatedRideTimeMinutes;
                    return projectedTotalMinutes <= 480;
                })
                .toList();

        if (eligibleDrivers.isEmpty()) {
            return Optional.empty();
        }

        return findBestDriverWithConflictResolution(eligibleDrivers, requestedScheduledAt, estimatedRideTimeMinutes);
    }

    private Optional<Driver> findBestDriverWithConflictResolution(List<Driver> eligibleDrivers,
            LocalDateTime referenceTime, int estimatedRideTimeMinutes) {
        LocalDateTime reference = referenceTime != null ? referenceTime : LocalDateTime.now();
        int requestedDuration = estimatedRideTimeMinutes + BUFFER_MINUTES;
        LocalDateTime requestedEnd = reference.plusMinutes(requestedDuration);
        List<Driver> noConflictDrivers = new ArrayList<>();
        Map<Driver, LocalDateTime> conflictEndTimes = new HashMap<>();

        // STEP 4: get all drivers rides
        for (Driver driver : eligibleDrivers) {
            List<Ride> driverRides = rideRepository.findByDriverId(driver.getId()).stream()
                    .filter(r -> r.getStatus() == RideStatus.CREATED || r.getStatus() == RideStatus.STARTED)
                    .toList();

            // if driver has no rides he is a match
            if (driverRides.isEmpty()) {
                noConflictDrivers.add(driver);
                continue;
            }

            // Build busy windows and sort them so we can find the first feasible gap.
            List<RideWindow> windows = new ArrayList<>();
            for (Ride existingRide : driverRides) {
                LocalDateTime rideStart = resolveRideStart(existingRide);
                int duration = getRideDurationMinutes(existingRide);
                LocalDateTime rideEnd = rideStart.plusMinutes(duration + BUFFER_MINUTES);
                windows.add(new RideWindow(rideStart, rideEnd));
            }
            windows.sort(Comparator.comparing(window -> window.start));

            // Priority 1: driver is free for the whole requested window.
            boolean overlapsRequestedWindow = windows.stream()
                    .anyMatch(window -> window.start.isBefore(requestedEnd) && window.end.isAfter(reference));

            if (!overlapsRequestedWindow) {
                noConflictDrivers.add(driver);
                continue;
            }

            // Priority 2: find the ride that overlaps the requested time, then see if the
            // post-overlap window can fit the full new ride without hitting another window.
            LocalDateTime possibleStart = reference;
            for (RideWindow window : windows) {
                if (window.start.isBefore(reference) && window.end.isAfter(reference)) {
                    possibleStart = window.end;
                    break;
                }
            }

            final LocalDateTime possibleStartFinal = possibleStart;
            LocalDateTime possibleEnd = possibleStartFinal.plusMinutes(requestedDuration);
            boolean overlapsAnotherWindow = windows.stream()
                    .anyMatch(window -> window.start.isBefore(possibleEnd)
                            && window.end.isAfter(possibleStartFinal));

            if (!overlapsAnotherWindow) {
                conflictEndTimes.put(driver, possibleStartFinal);
            }

        }

        // Priority 1: Return drivers with no conflicts
        if (!noConflictDrivers.isEmpty()) {
            return noConflictDrivers.stream().findFirst();
        }

        // Priority 2: Return driver with earliest conflict end time (fastest available)
        if (!conflictEndTimes.isEmpty()) {
            return conflictEndTimes.entrySet().stream()
                    .min(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey);
        }

        return Optional.empty();
    }

    private LocalDateTime resolveRideStart(Ride ride) {
        // For started rides, use actual start time
        if (ride.getStatus() == RideStatus.STARTED) {
            return ride.getStartedAt();
        }

        // For created rides, use scheduled time or now if not scheduled
        if (ride.getScheduledAt() != null) {
            return ride.getScheduledAt();
        }

        return LocalDateTime.now();
    }

    private static final class RideWindow {
        private final LocalDateTime start;
        private final LocalDateTime end;

        private RideWindow(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
    }

    private int getRideDurationMinutes(Ride ride) {
        return ride.getEstimatedDurationMinutes() != null
                ? ride.getEstimatedDurationMinutes()
                : DEFAULT_ESTIMATED_MINUTES;
    }

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
            System.err.println("Failed to estimate ride time, using default: " + e.getMessage());
            return DEFAULT_ESTIMATED_MINUTES;
        }
    }
}
