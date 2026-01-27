package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsById(Long id);
}
