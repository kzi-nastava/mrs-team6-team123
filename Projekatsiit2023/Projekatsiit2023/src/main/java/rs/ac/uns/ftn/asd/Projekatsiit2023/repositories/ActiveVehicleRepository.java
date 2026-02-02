package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;

import java.util.Optional;

public interface ActiveVehicleRepository extends JpaRepository<ActiveVehicle, Long> {
    Optional<ActiveVehicle> findByVehicleId(Long vehicleId);
    Optional<ActiveVehicle> findByCurrentRideId(Long rideId);
}
