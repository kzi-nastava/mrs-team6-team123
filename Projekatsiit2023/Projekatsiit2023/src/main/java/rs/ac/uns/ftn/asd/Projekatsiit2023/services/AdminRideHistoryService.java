// AdminRideHistoryService.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.AdminRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.PassengerInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRideHistoryService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;

    public AdminRideHistoryService(RideRepository rideRepository,
                                   UserRepository userRepository,
                                   DriverRepository driverRepository,
                                   PassengerRepository passengerRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.passengerRepository = passengerRepository;
    }

    public List<AdminRideHistoryDTO> getUserRideHistory(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            String sortBy,
            String sortOrder) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Ride> userRides;

        if (driverRepository.existsById(userId)) {
            userRides = rideRepository.findByDriverId(userId);
        } else if (passengerRepository.existsById(userId)) {
            List<Ride> allRides = rideRepository.findAll();
            userRides = allRides.stream()
                    .filter(ride ->
                            ride.getCreator().getId().equals(userId) ||
                            ride.getPassengers().stream().anyMatch(p -> p.getId().equals(userId))
                    )
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("User is neither a driver nor a passenger");
        }

        userRides = userRides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.FINISHED)
                .collect(Collectors.toList());

        if (fromDate != null) {
            userRides = userRides.stream()
                    .filter(ride -> !ride.getDate().isBefore(fromDate))
                    .collect(Collectors.toList());
        }
        if (toDate != null) {
            userRides = userRides.stream()
                    .filter(ride -> !ride.getDate().isAfter(toDate))
                    .collect(Collectors.toList());
        }

        List<AdminRideHistoryDTO> dtos = userRides.stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());

        dtos = sortRides(dtos, sortBy, sortOrder);

        return dtos;
    }

    public List<AdminRideHistoryDTO> getAllRideHistory(
            LocalDate fromDate,
            LocalDate toDate,
            String sortBy,
            String sortOrder) {

        List<Ride> allRides = rideRepository.findAll();

        allRides = allRides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.FINISHED)
                .collect(Collectors.toList());

        if (fromDate != null) {
            allRides = allRides.stream()
                    .filter(ride -> !ride.getDate().isBefore(fromDate))
                    .collect(Collectors.toList());
        }
        if (toDate != null) {
            allRides = allRides.stream()
                    .filter(ride -> !ride.getDate().isAfter(toDate))
                    .collect(Collectors.toList());
        }

        List<AdminRideHistoryDTO> dtos = allRides.stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());

        dtos = sortRides(dtos, sortBy, sortOrder);

        return dtos;
    }

    public AdminRideHistoryDTO getRideDetails(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        return mapToAdminDTO(ride);
    }

    private AdminRideHistoryDTO mapToAdminDTO(Ride ride) {
        AdminRideHistoryDTO dto = new AdminRideHistoryDTO();

        dto.setRideId(ride.getId());
        dto.setStartLocation(ride.getStartLocation());
        dto.setEndLocation(ride.getEndLocation());
        dto.setStartedAt(ride.getStartedAt());
        dto.setEndedAt(ride.getEndedAt());
        dto.setDate(ride.getDate());
        dto.setPrice(ride.getPrice());
        dto.setTotalDistance(ride.getTotalDistance());

        dto.setStartLat(ride.getRoute().getStartLatitude());
        dto.setStartLng(ride.getRoute().getStartLongitude());
        dto.setEndLat(ride.getEndLatitude());
        dto.setEndLng(ride.getEndLongitude());

        dto.setDriverId(ride.getDriver().getId());
        dto.setDriverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        dto.setDriverPhoto(ride.getDriver().getProfileImage());

        dto.setCreatorId(ride.getCreator().getId());
        dto.setCreatorName(ride.getCreator().getFirstName() + " " + ride.getCreator().getLastName());

        List<PassengerInfoDTO> passengers = ride.getPassengers().stream()
                .map(this::mapPassengerToInfo)
                .collect(Collectors.toList());
        dto.setPassengers(passengers);

        if (ride.getCanceledBy() != null) {
            dto.setCancelled(true);
            dto.setCancelledByUserId(ride.getCanceledBy().getId());
            dto.setCancelledByName(ride.getCanceledBy().getFirstName() + " " + ride.getCanceledBy().getLastName());
            dto.setCancelledByRole(ride.getCanceledBy().getUserRole().toString());
        } else {
            dto.setCancelled(false);
        }

        dto.setPanicTriggered(ride.isPanicTriggered());

        dto.setDriverRating(ride.getDriverRating());
        dto.setVehicleRating(ride.getVehicleRating());
        dto.setRated(ride.isRideRated());

        List<String> reports = ride.getIrregularityReports().stream()
                .map(report -> report.getDescription())
                .collect(Collectors.toList());
        dto.setInconsistencyReports(reports);
        
        dto.setRouteId(ride.getRoute().getId());

        return dto;
    }

    private PassengerInfoDTO mapPassengerToInfo(Passenger passenger) {
        PassengerInfoDTO dto = new PassengerInfoDTO();
        dto.setId(passenger.getId());
        dto.setName(passenger.getFirstName() + " " + passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setProfileImage(passenger.getProfileImage());
        return dto;
    }

    private List<AdminRideHistoryDTO> sortRides(
            List<AdminRideHistoryDTO> rides,
            String sortBy,
            String sortOrder) {

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "date";
        }
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "desc";
        }

        Comparator<AdminRideHistoryDTO> comparator;

        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getPrice);
                break;
            case "startlocation":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getStartLocation);
                break;
            case "endlocation":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getEndLocation);
                break;
            case "startedat":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getStartedAt);
                break;
            case "endedat":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getEndedAt);
                break;
            case "totaldistance":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getTotalDistance);
                break;
            case "drivername":
                comparator = Comparator.comparing(AdminRideHistoryDTO::getDriverName);
                break;
            case "date":
            default:
                comparator = Comparator.comparing(AdminRideHistoryDTO::getDate)
                        .thenComparing(AdminRideHistoryDTO::getStartedAt);
                break;
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return rides.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}