package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);

    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId AND r.status IN :statuses")
    List<Ride> findByDriverIdAndStatusIn(@Param("driverId") Long driverId,
            @Param("statuses") List<RideStatus> statuses);

    Long driver(Driver driver);

    List<Ride> findByStatus(RideStatus status);

    @Query("SELECT r FROM Ride r WHERE r.scheduledAt IS NOT NULL AND r.status = 'CREATED'")
    List<Ride> findScheduledRides();

    @Query("SELECT r FROM Ride r WHERE r.status = 'FINISHED' AND (r.creator.id = :passengerId OR :passengerId IN (SELECT p.id FROM r.passengers p))")
    List<Ride> findFinishedRidesByPassengerId(@Param("passengerId") Long passengerId);

    @Query("SELECT r FROM Ride r WHERE r.status = 'FINISHED' AND r.driver.id = :driverId")
    List<Ride> findFinishedRidesByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT r FROM Ride r WHERE r.status = 'FINISHED' ORDER BY r.date DESC")
    List<Ride> findAllFinishedRides();
}
