package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByActive(boolean active);

    @Query("""
            select d
            from Driver d
            where not exists (
                select 1 from Ride r
                where r.driver = d and r.status = :status
            )
            """)
    List<Driver> findAllWithoutRideStatus(@Param("status") RideStatus status);

    @Query("""
            select d
            from Driver d
            where not exists (
                select 1 from Ride r
                where r.driver = d and r.status = :started
            )
            """)
    List<Driver> findAllWithoutStartedRide(@Param("started") RideStatus started);

    @Query("""
            select d
            from Driver d
            where exists (
                select 1 from Ride r
                where r.driver = d and r.status = :started
            )
            and not exists (
                select 1 from Ride r2
                where r2.driver = d and r2.status = :created
            )
            """)
    List<Driver> findAllWithStartedButNoCreated(@Param("started") RideStatus started,
            @Param("created") RideStatus created);

    @Query("""
            select d
            from Driver d
            where d.vehicle is not null
            and not exists (
                select 1 from Ride r
                where r.driver = d and r.status = :started
            )
            and (:babySeat = false or d.vehicle.babyTransport = true)
            and (:petFriendly = false or d.vehicle.petTransport = true)
            """)
    List<Driver> findAvailableDrivers(@Param("started") RideStatus started,
            @Param("babySeat") boolean babySeat,
            @Param("petFriendly") boolean petFriendly);
}