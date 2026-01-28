package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActiveVehicleRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class RouteService {
    private final ActiveVehicleRepository activeVehicleRepository;
    private final GraphHopperService graphHopperService;

    public RouteService(
            ActiveVehicleRepository activeVehicleRepository,
            GraphHopperService graphHopperService) {
        this.activeVehicleRepository = activeVehicleRepository;
        this.graphHopperService = graphHopperService;
    }

    @Transactional
    public void calculateAndSaveRoute(ActiveVehicle vehicle) {
        if (hasNoTargetCoordinates(vehicle))
            throw new IllegalArgumentException("Target coordinates are not set");

        GeoPointDTO startPoint = new GeoPointDTO();
        startPoint.setLatitude(vehicle.getCurrentLatitude());
        startPoint.setLongitude(vehicle.getCurrentLongitude());
        GeoPointDTO endPoint = new GeoPointDTO();
        endPoint.setLatitude(vehicle.getTargetLatitude());
        endPoint.setLongitude(vehicle.getTargetLongitude());

        List<GeoPointDTO> points = List.of(startPoint, endPoint);

        List<GeoPointDTO> routePoints = graphHopperService.getRoutePoints(points);

        try {
            ObjectMapper mapper = new ObjectMapper();
            vehicle.setRouteCoordinates(mapper.writeValueAsString(routePoints));
            vehicle.setRouteIndex(0);

            activeVehicleRepository.save(vehicle);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize route", e);
        }
    }

    private boolean hasNoTargetCoordinates(ActiveVehicle vehicle) {
        return vehicle.getTargetLatitude() == 0 || vehicle.getTargetLongitude() == 0;
    }
}
