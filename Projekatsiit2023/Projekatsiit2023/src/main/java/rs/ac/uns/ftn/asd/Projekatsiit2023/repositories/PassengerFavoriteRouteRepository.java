package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PassengerFavoriteRoute;

import java.util.Optional;

public interface PassengerFavoriteRouteRepository extends JpaRepository<PassengerFavoriteRoute, Long> {
    Optional<PassengerFavoriteRoute> findById(Long id);
}
