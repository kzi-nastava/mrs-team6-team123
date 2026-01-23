package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RideHistoryService {
    private final RideRepository rideRepository;

    public RideHistoryService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<DriverRideHistoryDTO> getDriverRideHistory(Long driverId, LocalDate from, LocalDate to) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        List<DriverRideHistoryDTO> rideHistory = new ArrayList<>();
        for (Ride ride : rides) {
            if ((from == null || !ride.getDate().isBefore(from)) &&
                    (to == null || !ride.getDate().isAfter(to))) {
                rideHistory.add(mapRideToRideHistoryDTO(ride));
            }
        }
        sortByDateDescending(rideHistory);
        return rideHistory;
    }

    private DriverRideHistoryDTO mapRideToRideHistoryDTO(Ride ride) {
        DriverRideHistoryDTO dto = new DriverRideHistoryDTO();
        dto.setRideId(ride.getId());
        for (var passenger : ride.getPassengers()) {
            dto.getPassengers().add(passenger.getFirstName() + " " + passenger.getLastName());
        }
        dto.setStartLocation(ride.getStartLocation());
        dto.setEndLocation(ride.getEndLocation());
        dto.setStartedAt(ride.getStartedAt());
        dto.setEndedAt(ride.getEndedAt());
        dto.setDate(ride.getDate());
        dto.setPrice(ride.getPrice());
        if (ride.isPanicTriggered())
            dto.setPanicTriggered("Yes");
        else
            dto.setPanicTriggered("No");
        if (ride.getCanceledBy() != null) {
            dto.setCanceledBy(ride.getCanceledBy().getFirstName() + " " + ride.getCanceledBy().getLastName());
        } else {
            dto.setCanceledBy("/");
        }
        dto.setStartLat(ride.getRoute().getStartLatitude());
        dto.setStartLng(ride.getRoute().getStartLongitude());
        dto.setEndLat(ride.getEndLatitude());
        dto.setEndLng(ride.getEndLongitude());
        for (var report : ride.getIrregularityReports()) {
            dto.getReports().add(report.getDescription());
        }
        return dto;
    }

    private void sortByDateDescending(List<DriverRideHistoryDTO> rideHistory) {
        rideHistory.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
    }
}
