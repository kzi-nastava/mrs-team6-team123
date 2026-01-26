package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PanicAlert;

import java.util.List;

@Repository
public interface PanicAlertRepository extends JpaRepository<PanicAlert, Long> {
    List<PanicAlert> findByRideId(Long rideId);
    List<PanicAlert> findByResolvedFalseOrderByTriggeredAtDesc();
    List<PanicAlert> findAllByOrderByTriggeredAtDesc();
}