// RideStopService.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StopRideResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;

import java.time.LocalTime;

@Service
public class RideStopService {

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;

    private static final double PRICE_PER_KM = 120.0;

    public RideStopService(RideRepository rideRepository, RouteRepository routeRepository) {
        this.rideRepository = rideRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public StopRideResponseDTO stopRide(Long rideId, StopRideRequestDTO request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Can only stop a ride that is in progress (STARTED status)");
        }

        if (ride.isRideStopped()) {
            throw new RuntimeException("Ride has already been stopped");
        }

        double[] coords = parseLocation(request.getCurrentLocation());
        double currentLat = coords[0];
        double currentLng = coords[1];

        Route route = ride.getRoute();
        double startLat = route.getStartLatitude();
        double startLng = route.getStartLongitude();
        
        double distanceTravelled = calculateHaversineDistance(startLat, startLng, currentLat, currentLng);

        double originalTotalDistance = ride.getTotalDistance();
        double originalPrice = ride.getPrice();
        
        double basePrice = originalPrice - (originalTotalDistance * PRICE_PER_KM);
        if (basePrice < 0) basePrice = 0;
        
        double newPrice = basePrice + (distanceTravelled * PRICE_PER_KM);
        newPrice = Math.round(newPrice * 100.0) / 100.0; 

        ride.setEndLocation(request.getCurrentLocation());
        ride.setEndLatitude(currentLat);
        ride.setEndLongitude(currentLng);
        ride.setEndedAt(request.getStoppedAt() != null ? request.getStoppedAt().toLocalTime() : LocalTime.now());
        ride.setPrice(newPrice);
        ride.setTotalDistance(distanceTravelled);
        ride.setRideStopped(true);
        ride.setStatus(RideStatus.FINISHED);

        route.setEndLocation(request.getCurrentLocation());
        route.setEndLatitude(currentLat);
        route.setEndLongitude(currentLng);
        routeRepository.save(route);

        rideRepository.save(ride);

        StopRideResponseDTO response = new StopRideResponseDTO();
        response.setRideId(rideId);
        response.setStoppedLocation(request.getCurrentLocation());
        response.setStoppedAt(request.getStoppedAt());
        response.setRecalculatedPrice(newPrice);
        response.setMessage("Ride stopped successfully. New destination: " + request.getCurrentLocation());

        return response;
    }

    private double[] parseLocation(String location) {
        try {
            String cleaned = location.replaceAll("[NSEW]", "").trim();
            String[] parts = cleaned.split(",");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid location format: " + location);
            }

            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                throw new IllegalArgumentException("Coordinates out of range");
            }

            return new double[]{lat, lon};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate format: " + location);
        }
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}