package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ActiveVehicleDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActiveVehicleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicMapService {
    private final ActiveVehicleRepository repository;
    private final DriverRepository driverRepository;

    @Autowired
    private RouteService routeService;

    private static final double ARRIVAL_THRESHOLD = 0.00005;

    public PublicMapService(
            ActiveVehicleRepository repository,
            DriverRepository driverRepository) {
        this.repository = repository;
        this.driverRepository = driverRepository;
    }

    public List<ActiveVehicleDTO> getVehicles() {
        List<ActiveVehicle> vehicles = repository.findAll();
        for (ActiveVehicle vehicle : vehicles) {
            tickVehicle(vehicle);
        }
        repository.saveAll(vehicles);

        List<ActiveVehicleDTO> result = new ArrayList<>();
        for (ActiveVehicle v : vehicles) {
            result.add(mapActiveVehicleToDTO(v));
        }
        return result;
    }

    public ActiveVehicleDTO getDriversVehicle(Long driverId) {
        System.out.println("get drivers active vehicle");
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        ActiveVehicle activeVehicle = repository.findByVehicleId(driver.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("Active vehicle not found"));
        tickVehicle(activeVehicle);
        repository.save(activeVehicle);
        return mapActiveVehicleToDTO(activeVehicle);
    }

    private ActiveVehicleDTO mapActiveVehicleToDTO(ActiveVehicle v) {
        return new ActiveVehicleDTO(
                v.getId(),
                v.getCurrentLatitude(),
                v.getCurrentLongitude(),
                v.isAvailable()
        );
    }

    private void tickVehicle(ActiveVehicle vehicle) {
        if (vehicle.isAvailable() && vehicle.getCurrentRide() == null) {
            ensureRandomMovement(vehicle);
            moveAlongRoute(vehicle);
            return;
        }

        if (!vehicle.isAvailable() && vehicle.getCurrentRide() != null) {
            Ride ride = vehicle.getCurrentRide();

            if (ride.getStatus() == RideStatus.STARTED) {
                moveAlongRoute(vehicle);

                if (hasArrived(vehicle)) {
                    ride.setStatus(RideStatus.ARRIVED);
                    freezeAtCurrentLocation(vehicle);
                }
            }

            if (ride.getStatus() == RideStatus.ARRIVED) {
                freezeAtCurrentLocation(vehicle);
            }
        }
    }


    private void moveAlongRoute(ActiveVehicle v) {
        if (v.getRouteCoordinates() == null || v.getRouteCoordinates().isEmpty()) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<GeoPointDTO> route = mapper.readValue(
                    v.getRouteCoordinates(),
                    new TypeReference<>() {}
            );

            int index = v.getRouteIndex();
            if (index >= route.size() && v.getCurrentRide() == null) {
                assignNewRandomTarget(v);
                routeService.calculateAndSaveRoute(v);
                v.setRouteIndex(0);
                return;
            }

            GeoPointDTO next = route.get(index);
            v.setCurrentLatitude(next.getLatitude());
            v.setCurrentLongitude(next.getLongitude());
            v.setRouteIndex(index + 1);

            repository.save(v);

        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize route", e);
        }
    }

    private boolean hasArrived(ActiveVehicle v) {
        return Math.abs(v.getCurrentLatitude() - v.getTargetLatitude()) < ARRIVAL_THRESHOLD &&
                Math.abs(v.getCurrentLongitude() - v.getTargetLongitude()) < ARRIVAL_THRESHOLD;
    }

    private void assignNewRandomTarget(ActiveVehicle v) {
        double baseLat = 45.25;
        double baseLon = 19.83;

        double randomLat = baseLat + (Math.random() - 0.5) * 0.04;
        double randomLon = baseLon + (Math.random() - 0.5) * 0.04;

        v.setTargetLatitude(randomLat);
        v.setTargetLongitude(randomLon);
    }

    private boolean hasNoTargetCoordinates(ActiveVehicle vehicle) {
        return vehicle.getTargetLatitude() == 0 || vehicle.getTargetLongitude() == 0;
    }

    private void ensureRandomMovement(ActiveVehicle v) {
        if (hasNoTargetCoordinates(v) || hasArrived(v)) {
            assignNewRandomTarget(v);
            routeService.calculateAndSaveRoute(v);
        }
    }

    private void freezeAtCurrentLocation(ActiveVehicle vehicle) {
        vehicle.setTargetLatitude(vehicle.getCurrentLatitude());
        vehicle.setTargetLongitude(vehicle.getCurrentLongitude());

        vehicle.setRouteCoordinates(null);

        vehicle.setRouteIndex(0);
    }


}
