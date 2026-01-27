// PassengerRideHistoryService.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.PassengerRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PassengerRideHistoryService {

    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;

    public PassengerRideHistoryService(RideRepository rideRepository, 
                                       PassengerRepository passengerRepository) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
    }

    /**
     * Vraća istoriju vožnji za putnika sa filterima i sortiranjem
     */
    public List<PassengerRideHistoryDTO> getPassengerRideHistory(
            Long passengerId,
            LocalDate fromDate,
            LocalDate toDate,
            String sortBy,
            String sortOrder) {

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        // Dobavi sve vožnje
        List<Ride> allRides = rideRepository.findAll();

        // Filtriraj vožnje gde je ovaj putnik učestvovao (kao kreator ili putnik)
        List<Ride> passengerRides = allRides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.FINISHED)
                .filter(ride -> 
                    ride.getCreator().getId().equals(passengerId) ||
                    ride.getPassengers().stream().anyMatch(p -> p.getId().equals(passengerId))
                )
                .collect(Collectors.toList());

        // Filtriraj po datumu
        if (fromDate != null) {
            passengerRides = passengerRides.stream()
                    .filter(ride -> !ride.getDate().isBefore(fromDate))
                    .collect(Collectors.toList());
        }
        if (toDate != null) {
            passengerRides = passengerRides.stream()
                    .filter(ride -> !ride.getDate().isAfter(toDate))
                    .collect(Collectors.toList());
        }

        // Mapiraj u DTO
        List<PassengerRideHistoryDTO> dtos = passengerRides.stream()
                .map(this::mapToPassengerDTO)
                .collect(Collectors.toList());

        // Sortiraj
        dtos = sortRides(dtos, sortBy, sortOrder);

        return dtos;
    }

    /**
     * Vraća detalje jedne vožnje za putnika
     */
    public PassengerRideHistoryDTO getRideDetails(Long rideId, Long passengerId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Proveri da li je putnik učestvovao u vožnji
        boolean isParticipant = ride.getCreator().getId().equals(passengerId) ||
                ride.getPassengers().stream().anyMatch(p -> p.getId().equals(passengerId));

        if (!isParticipant) {
            throw new RuntimeException("You are not a participant of this ride");
        }

        return mapToPassengerDTO(ride);
    }

    private PassengerRideHistoryDTO mapToPassengerDTO(Ride ride) {
        PassengerRideHistoryDTO dto = new PassengerRideHistoryDTO();
        
        dto.setRideId(ride.getId());
        dto.setStartLocation(ride.getStartLocation());
        dto.setEndLocation(ride.getEndLocation());
        dto.setStartedAt(ride.getStartedAt());
        dto.setEndedAt(ride.getEndedAt());
        dto.setDate(ride.getDate());
        dto.setPrice(ride.getPrice());
        
        // Koordinate za mapu
        dto.setStartLat(ride.getRoute().getStartLatitude());
        dto.setStartLng(ride.getRoute().getStartLongitude());
        dto.setEndLat(ride.getEndLatitude());
        dto.setEndLng(ride.getEndLongitude());
        
        // Vozač
        dto.setDriverId(ride.getDriver().getId());
        dto.setDriverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        dto.setDriverPhoto(ride.getDriver().getProfileImage());
        dto.setDriverRating(ride.getDriver().getRating());
        
        // Ocene ove vožnje
        dto.setRideDriverRating(ride.getDriverRating());
        dto.setRideVehicleRating(ride.getVehicleRating());
        dto.setRated(ride.isRideRated());
        
        // Prijave nekonzistentnosti
        List<String> reports = ride.getIrregularityReports().stream()
                .map(report -> report.getDescription())
                .collect(Collectors.toList());
        dto.setInconsistencyReports(reports);
        
        // Ruta za ponovo poručivanje
        dto.setRouteId(ride.getRoute().getId());
        
        return dto;
    }

    private List<PassengerRideHistoryDTO> sortRides(
            List<PassengerRideHistoryDTO> rides,
            String sortBy,
            String sortOrder) {

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "date";
        }
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "desc";
        }

        Comparator<PassengerRideHistoryDTO> comparator;

        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getPrice);
                break;
            case "startlocation":
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getStartLocation);
                break;
            case "endlocation":
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getEndLocation);
                break;
            case "startedat":
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getStartedAt);
                break;
            case "endedat":
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getEndedAt);
                break;
            case "date":
            default:
                comparator = Comparator.comparing(PassengerRideHistoryDTO::getDate)
                        .thenComparing(PassengerRideHistoryDTO::getStartedAt);
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