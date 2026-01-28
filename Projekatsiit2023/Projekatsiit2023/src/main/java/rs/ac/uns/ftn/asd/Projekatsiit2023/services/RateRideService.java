package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideRatingRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideRatingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.RideRating;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRatingRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class RateRideService {
    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final RideRatingRepository rideRatingRepository;

    public RateRideService(
            RideRepository rideRepository,
            PassengerRepository passengerRepository,
            RideRatingRepository rideRatingRepository) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.rideRatingRepository = rideRatingRepository;
    }

    public RideRatingRequestDTO getRideForRating(Long rideId) {
        var drive = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + rideId));
        return mapRideToRideRatingRequestDTO(drive);
    }

    public void rateRide(RideRatingResponseDTO dto) {
        RideRating rideRating = new RideRating();
        var ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + dto.getRideId()));
        if (!canBeRated(ride.getDate()))
            throw new IllegalStateException("Ride can no longer be rated.");
        rideRating.setRide(ride);
        if (alreadyRated(dto.getAuthorId(), dto.getRideId()))
            throw new IllegalStateException("Passenger has already rated this ride.");
        var author = passengerRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found with id: " + dto.getAuthorId()));
        rideRating.setAuthor(author);
        rideRating.setDriverRating(dto.getDriverRating());
        rideRating.setVehicleRating(dto.getVehicleRating());
        rideRating.setComment(dto.getComment());
        rideRatingRepository.save(rideRating);
        ride.setRideRated(true);
        List<RideRating> ratings = rideRatingRepository.findByRideId(ride.getId());
        ride.setDriverRating((ride.getDriverRating() + dto.getDriverRating())/ratings.size());
        ride.setVehicleRating((ride.getVehicleRating() + dto.getVehicleRating())/ratings.size());
        rideRepository.save(ride);
    }

    private RideRatingRequestDTO mapRideToRideRatingRequestDTO(Ride ride) {
        RideRatingRequestDTO dto = new RideRatingRequestDTO();
        dto.setRideId(ride.getId());
        dto.setDriverId(ride.getDriver().getId());
        dto.setVehicleId(ride.getDriver().getVehicle().getId());
        dto.setDriver(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        dto.setLicencePlate(ride.getDriver().getVehicle().getLicensePlate());
        return dto;
    }

    private boolean alreadyRated(Long passengerId, Long rideId) {
        List<RideRating> existingRatings = rideRatingRepository.findByAuthorId(passengerId);
        List<RideRating> rideRatings = rideRatingRepository.findByRideId(rideId);
        for (RideRating rating : existingRatings) {
            if (rideRatings.contains(rating)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeRated(LocalDate date) {
        LocalDate today = LocalDate.now();
        return !date.isBefore(today.minusDays(3));
    }
}
