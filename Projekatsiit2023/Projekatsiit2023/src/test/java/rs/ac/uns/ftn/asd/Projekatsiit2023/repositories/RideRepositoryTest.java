package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Driver driver1;
    private Driver driver2;
    private Passenger passenger1;
    private Route route;

    @BeforeEach
    void setUp() {
        // Cleanup
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        routeRepository.deleteAll();

        // Create and save vehicle + driver1
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleModel("Toyota Prius");
        vehicle1.setVehicleType(VehicleType.STANDARD);
        vehicle1.setLicensePlate("NS-001-AB");
        vehicle1.setSeats(4);
        vehicle1.setBabyTransport(false);
        vehicle1.setPetTransport(false);

        driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword("password");
        driver1.setFirstName("Marko");
        driver1.setLastName("Markovic");
        driver1.setAddress("Bulevar Oslobodjenja 1");
        driver1.setPhone("0601234567");
        driver1.setProfileImage("");
        driver1.setUserRole(UserRole.DRIVER);
        driver1.setAccountActivated(true);
        driver1.setAccountBlocked(false);
        driver1.setActive(true);
        driver1.setActiveMinutesLast24h(0);
        driver1.setTotalRides(0);
        driver1.setRating(0.0);
        driver1.setVehicle(vehicle1);
        driver1 = driverRepository.save(driver1);

        // Create and save vehicle + driver2
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleModel("BMW X5");
        vehicle2.setVehicleType(VehicleType.LUXURY);
        vehicle2.setLicensePlate("NS-002-CD");
        vehicle2.setSeats(4);
        vehicle2.setBabyTransport(true);
        vehicle2.setPetTransport(true);

        driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword("password");
        driver2.setFirstName("Jovan");
        driver2.setLastName("Jovanovic");
        driver2.setAddress("Futoska 5");
        driver2.setPhone("0607654321");
        driver2.setProfileImage("");
        driver2.setUserRole(UserRole.DRIVER);
        driver2.setAccountActivated(true);
        driver2.setAccountBlocked(false);
        driver2.setActive(true);
        driver2.setActiveMinutesLast24h(0);
        driver2.setTotalRides(0);
        driver2.setRating(0.0);
        driver2.setVehicle(vehicle2);
        driver2 = driverRepository.save(driver2);

        // Create and save passenger
        passenger1 = new Passenger();
        passenger1.setEmail("passenger1@test.com");
        passenger1.setPassword("password");
        passenger1.setFirstName("Ana");
        passenger1.setLastName("Anic");
        passenger1.setAddress("Strazilovska 10");
        passenger1.setPhone("0611111111");
        passenger1.setProfileImage("");
        passenger1.setUserRole(UserRole.PASSENGER);
        passenger1.setAccountActivated(true);
        passenger1.setAccountBlocked(false);
        passenger1.setStartedRide(false);
        passenger1 = passengerRepository.save(passenger1);

        // Create and save route
        route = new Route();
        route.setStartLocation("45.2511,19.8367");
        route.setEndLocation("45.2671,19.8335");
        route.setStartLatitude(45.2511);
        route.setStartLongitude(19.8367);
        route.setEndLatitude(45.2671);
        route.setEndLongitude(19.8335);
        route = routeRepository.save(route);
    }

    private Ride createAndSaveRide(Driver driver, RideStatus status, boolean stopped) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setCreator(passenger1);
        ride.setRoute(route);
        ride.setStartLocation("45.2511,19.8367");
        ride.setEndLocation("45.2671,19.8335");
        ride.setDate(LocalDate.now());
        ride.setStatus(status);
        ride.setPrice(500.0);
        ride.setTotalDistance(3.0);
        ride.setPanicTriggered(false);
        ride.setRideRated(false);
        ride.setDriverReported(false);
        ride.setRideStopped(stopped);
        return rideRepository.save(ride);
    }

    // ==================== findByDriverId ====================

    @Nested
    @DisplayName("findByDriverId tests")
    class FindByDriverIdTests {

        @Test
        @DisplayName("Should return rides for a specific driver")
        void shouldReturnRidesForDriver() {
            createAndSaveRide(driver1, RideStatus.STARTED, false);
            createAndSaveRide(driver1, RideStatus.FINISHED, false);
            createAndSaveRide(driver2, RideStatus.STARTED, false);

            List<Ride> rides = rideRepository.findByDriverId(driver1.getId());

            assertEquals(2, rides.size());
            rides.forEach(r -> assertEquals(driver1.getId(), r.getDriver().getId()));
        }

        @Test
        @DisplayName("Should return empty list when driver has no rides")
        void shouldReturnEmptyWhenNoRides() {
            List<Ride> rides = rideRepository.findByDriverId(driver1.getId());

            assertTrue(rides.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for non-existent driver ID")
        void shouldReturnEmptyForNonExistentDriver() {
            List<Ride> rides = rideRepository.findByDriverId(9999L);

            assertTrue(rides.isEmpty());
        }
    }

    // ==================== findByDriverIdAndStatusIn ====================

    @Nested
    @DisplayName("findByDriverIdAndStatusIn tests")
    class FindByDriverIdAndStatusInTests {

        @Test
        @DisplayName("Should return rides matching driver and statuses")
        void shouldReturnMatchingRides() {
            createAndSaveRide(driver1, RideStatus.STARTED, false);
            createAndSaveRide(driver1, RideStatus.FINISHED, false);
            createAndSaveRide(driver1, RideStatus.CREATED, false);

            List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(
                    driver1.getId(),
                    Arrays.asList(RideStatus.STARTED, RideStatus.CREATED));

            assertEquals(2, rides.size());
            rides.forEach(r ->
                    assertTrue(r.getStatus() == RideStatus.STARTED || r.getStatus() == RideStatus.CREATED));
        }

        @Test
        @DisplayName("Should return empty when no rides match status")
        void shouldReturnEmptyWhenNoStatusMatch() {
            createAndSaveRide(driver1, RideStatus.FINISHED, false);

            List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(
                    driver1.getId(),
                    Arrays.asList(RideStatus.STARTED));

            assertTrue(rides.isEmpty());
        }

        @Test
        @DisplayName("Should not return rides from other drivers")
        void shouldNotReturnOtherDriversRides() {
            createAndSaveRide(driver1, RideStatus.STARTED, false);
            createAndSaveRide(driver2, RideStatus.STARTED, false);

            List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(
                    driver1.getId(),
                    Arrays.asList(RideStatus.STARTED));

            assertEquals(1, rides.size());
            assertEquals(driver1.getId(), rides.get(0).getDriver().getId());
        }
    }

    // ==================== findByStatus ====================

    @Nested
    @DisplayName("findByStatus tests")
    class FindByStatusTests {

        @Test
        @DisplayName("Should return all rides with given status")
        void shouldReturnRidesWithStatus() {
            createAndSaveRide(driver1, RideStatus.STARTED, false);
            createAndSaveRide(driver2, RideStatus.STARTED, false);
            createAndSaveRide(driver1, RideStatus.FINISHED, false);

            List<Ride> rides = rideRepository.findByStatus(RideStatus.STARTED);

            assertEquals(2, rides.size());
            rides.forEach(r -> assertEquals(RideStatus.STARTED, r.getStatus()));
        }

        @Test
        @DisplayName("Should return empty list when no rides have given status")
        void shouldReturnEmptyWhenNoMatch() {
            createAndSaveRide(driver1, RideStatus.FINISHED, false);

            List<Ride> rides = rideRepository.findByStatus(RideStatus.CANCELLED);

            assertTrue(rides.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when no rides exist")
        void shouldReturnEmptyWhenNoRides() {
            List<Ride> rides = rideRepository.findByStatus(RideStatus.STARTED);

            assertTrue(rides.isEmpty());
        }
    }

    // ==================== findScheduledRides ====================

    @Nested
    @DisplayName("findScheduledRides tests")
    class FindScheduledRidesTests {

        @Test
        @DisplayName("Should return rides that are scheduled and have CREATED status")
        void shouldReturnScheduledCreatedRides() {
            Ride scheduledRide = createAndSaveRide(driver1, RideStatus.CREATED, false);
            scheduledRide.setScheduledAt(LocalDateTime.now().plusHours(2));
            rideRepository.save(scheduledRide);

            // Non-scheduled ride (scheduledAt is null)
            createAndSaveRide(driver2, RideStatus.CREATED, false);

            List<Ride> rides = rideRepository.findScheduledRides();

            assertEquals(1, rides.size());
            assertNotNull(rides.get(0).getScheduledAt());
            assertEquals(RideStatus.CREATED, rides.get(0).getStatus());
        }

        @Test
        @DisplayName("Should not return scheduled rides that are not CREATED")
        void shouldNotReturnNonCreatedScheduledRides() {
            Ride startedRide = createAndSaveRide(driver1, RideStatus.STARTED, false);
            startedRide.setScheduledAt(LocalDateTime.now().plusHours(2));
            rideRepository.save(startedRide);

            List<Ride> rides = rideRepository.findScheduledRides();

            assertTrue(rides.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when no scheduled rides exist")
        void shouldReturnEmptyWhenNoScheduledRides() {
            createAndSaveRide(driver1, RideStatus.CREATED, false);

            List<Ride> rides = rideRepository.findScheduledRides();

            assertTrue(rides.isEmpty());
        }

        @Test
        @DisplayName("Should return multiple scheduled rides")
        void shouldReturnMultipleScheduledRides() {
            Ride r1 = createAndSaveRide(driver1, RideStatus.CREATED, false);
            r1.setScheduledAt(LocalDateTime.now().plusHours(1));
            rideRepository.save(r1);

            Ride r2 = createAndSaveRide(driver2, RideStatus.CREATED, false);
            r2.setScheduledAt(LocalDateTime.now().plusHours(3));
            rideRepository.save(r2);

            List<Ride> rides = rideRepository.findScheduledRides();

            assertEquals(2, rides.size());
        }
    }
}