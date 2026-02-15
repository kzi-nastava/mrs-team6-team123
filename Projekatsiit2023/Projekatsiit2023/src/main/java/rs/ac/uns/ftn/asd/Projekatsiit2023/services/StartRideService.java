package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActiveVehicleRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartRideService {
    private final RideRepository rideRepository;
    private final ActiveVehicleRepository activeVehicleRepository;
    private final GraphHopperService graphHopperService;

    public StartRideService(
            RideRepository rideRepository,
            ActiveVehicleRepository activeVehicleRepository,
            GraphHopperService graphHopperService
    ) {
        this.rideRepository = rideRepository;
        this.activeVehicleRepository = activeVehicleRepository;
        this.graphHopperService = graphHopperService;
    }

    public void setActiveVehicle(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));
        Driver driver = ride.getDriver();
        ActiveVehicle activeVehicle = activeVehicleRepository.findByVehicleId(driver.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found - driver is not active"));
        activeVehicle.setCurrentRide(ride);
        activeVehicle.setAvailable(false);
        List<GeoPointDTO> stops = getGeoPointDTOS(ride);
        List<GeoPointDTO> routeStops = graphHopperService.getRoutePoints(stops);
        try {
            ObjectMapper mapper = new ObjectMapper();
            activeVehicle.setRouteCoordinates(mapper.writeValueAsString(routeStops));
            activeVehicle.setRouteIndex(0);
            activeVehicleRepository.save(activeVehicle);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize route", e);
        }
    }

    private static @NonNull List<GeoPointDTO> getGeoPointDTOS(Ride ride) {
        List<GeoPointDTO> stops = new ArrayList<>();
        GeoPointDTO start = new GeoPointDTO();
        start.setLatitude(ride.getRoute().getStartLatitude());
        start.setLongitude(ride.getRoute().getStartLongitude());
        start.setLocation(ride.getRoute().getStartLocation());
        stops.add(start);
        if (!ride.getRoute().getStops().isEmpty()) {
            for (var stop : ride.getRoute().getStops()) {
                GeoPointDTO stopPoint = new GeoPointDTO();
                stopPoint.setLatitude(stop.getLatitude());
                stopPoint.setLongitude(stop.getLongitude());
                stopPoint.setLocation(stop.getLocation());
                stops.add(stopPoint);
            }
        }
        GeoPointDTO end = new GeoPointDTO();
        end.setLatitude(ride.getRoute().getEndLatitude());
        end.setLongitude(ride.getRoute().getEndLongitude());
        end.setLocation(ride.getRoute().getEndLocation());
        stops.add(end);
        return stops;
    }
}
