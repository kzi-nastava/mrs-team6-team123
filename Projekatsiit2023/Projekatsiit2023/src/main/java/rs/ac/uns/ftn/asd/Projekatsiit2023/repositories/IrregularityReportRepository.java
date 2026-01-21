package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.IrregularityReport;

import java.util.List;

@Repository
public interface IrregularityReportRepository extends JpaRepository<IrregularityReport, Long> {
    List<IrregularityReport> findByRideId(Long rideId);
    List<IrregularityReport> findByAuthorId(Long authorId);
}
