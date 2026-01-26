package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.ChangeStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PendingDriverProfileChange;

import java.util.List;

@Repository
public interface PendingDriverProfileChangeRepository extends JpaRepository<PendingDriverProfileChange, Long> {

    List<PendingDriverProfileChange> findByStatus(ChangeStatus status);

    List<PendingDriverProfileChange> findByDriverIdAndStatus(Long driverId, ChangeStatus status);
}
