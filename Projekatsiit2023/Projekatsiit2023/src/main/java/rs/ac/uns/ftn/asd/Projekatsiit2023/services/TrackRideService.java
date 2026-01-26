package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideTrackingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.Optional;

@Service
public class TrackRideService {
    private final RideRepository repository;
    double AVERAGE_SPEED_KMH = 45.0;

    public TrackRideService(RideRepository repository) {
        this.repository = repository;
    }

    public RideTrackingResponseDTO trackRide(Long rideId) throws RuntimeException {
        Optional<Ride> ride = repository.findById(rideId);
        if (ride.isEmpty()) {
            throw new RuntimeException("Ride not found");
        }
        RideTrackingResponseDTO dto = mapRideToRideTrackingDTO(ride);
        return dto;
    }

    public RideTrackingResponseDTO mapRideToRideTrackingDTO(Optional<Ride> ride) {
        RideTrackingResponseDTO dto = new RideTrackingResponseDTO();
        dto.setRideId(ride.get().getId());
        dto.setDriverId(ride.get().getDriver().getId());
        dto.getStops().add(mapStopToGeoPointDTO(
                ride.get().getRoute().getStartLatitude(),
                ride.get().getRoute().getStartLongitude(),
                ride.get().getRoute().getStartLocation()));
        for (var stop : ride.get().getRoute().getStops()) {
            dto.getStops().add(mapStopToGeoPointDTO(
                    stop.getLatitude(),
                    stop.getLongitude(),
                    stop.getLocation()));
        }
        dto.getStops().add(mapStopToGeoPointDTO(
                ride.get().getRoute().getEndLatitude(),
                ride.get().getRoute().getEndLongitude(),
                ride.get().getRoute().getEndLocation()
        ));
        dto.setStopsMade(ride.get().getStopsMade());
        setRideInfo(dto, ride.get());
        return dto;
    }

    private GeoPointDTO mapStopToGeoPointDTO(double lat, double lng, String location) {
        GeoPointDTO geoPointDTO = new GeoPointDTO();
        geoPointDTO.setLatitude(lat);
        geoPointDTO.setLongitude(lng);
        geoPointDTO.setLocation(location);
        return geoPointDTO;
    }

    private int calculateDuration(Ride ride) {
        double remainingTimeHours = ride.getTotalDistance() / AVERAGE_SPEED_KMH;
        return (int) Math.ceil(remainingTimeHours * 60);
    }

    private void setRideInfo(RideTrackingResponseDTO dto, Ride ride) {
        dto.getInfo().setDriver(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        dto.getInfo().setStartedAt(ride.getStartedAt().toString());
        dto.getInfo().setFrom(ride.getRoute().getStartLocation());
        dto.getInfo().setTo(ride.getRoute().getEndLocation());
        dto.getInfo().setPrice(ride.getPrice());
        for (var passenger : ride.getPassengers()) {
            dto.getInfo().getPassengers().add(passenger.getFirstName() + " " + passenger.getLastName());
        }
        dto.getInfo().setDuration(calculateDuration(ride));
        for (var report : ride.getIrregularityReports()) {
            dto.getInfo().getReports().add(report.getDescription());
        }
    }
}
