package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.RideRating;

import java.util.List;

@Repository
public interface RideRatingRepository extends JpaRepository<RideRating, Long> {
    List<RideRating> findByRideId(Long rideId);
    List<RideRating> findByAuthorId(Long vehicleId);
}
