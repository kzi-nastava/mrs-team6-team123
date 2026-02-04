package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideMonitoringResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RideMonitoringService {
    private final RideRepository rideRepository;

    public RideMonitoringService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<RideMonitoringResponseDTO> getAllActiveRides() {
        List<Ride> activeRides = rideRepository.findByStatus(RideStatus.STARTED);
        List<RideMonitoringResponseDTO> response = new ArrayList<>();
        for (Ride ride : activeRides) {
            response.add(mapRideToMonitoringDTO(ride));
        }
        return response;
    }

    private RideMonitoringResponseDTO mapRideToMonitoringDTO(Ride ride) {
        RideMonitoringResponseDTO dto = new RideMonitoringResponseDTO();
        dto.setRideId(ride.getId());
        dto.setDriverId(ride.getDriver().getId());
        dto.setDriverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        dto.setLicencePlate(ride.getDriver().getVehicle().getLicensePlate());
        dto.setFrom(ride.getStartLocation());
        dto.setTo(ride.getRoute().getEndLocation());
        return dto;
    }
}
