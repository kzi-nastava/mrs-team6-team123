package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("Driver Repository Tests - Ride Ordering Functionality")
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    private Driver activeDriver;
    private Driver inactiveDriver;
    private Passenger testPassenger;
    private Vehicle standardVehicle;
    private Vehicle luxuryVehicle;

    @BeforeEach
    void setUp() {
        // Generate unique values to avoid constraint conflicts from previous runs
        long timestamp = System.currentTimeMillis() % 100000;
        String licensePlate1 = "BG-" + (100 + (timestamp % 800)) + "-AA";
        String licensePlate2 = "BG-" + (100 + ((timestamp + 1) % 800)) + "-BB";
        String activeEmail = "active" + timestamp + "@test.com";
        String inactiveEmail = "inactive" + timestamp + "@test.com";
        String passengerEmail = "passenger" + timestamp + "@test.com";

        // Setup vehicles
        standardVehicle = new Vehicle();
        standardVehicle.setVehicleType(VehicleType.STANDARD);
        standardVehicle.setVehicleModel("Toyota Corolla");
        standardVehicle.setLicensePlate(licensePlate1);
        standardVehicle.setSeats(4);
        standardVehicle.setBabyTransport(false);
        standardVehicle.setPetTransport(false);
        standardVehicle = vehicleRepository.save(standardVehicle);

        luxuryVehicle = new Vehicle();
        luxuryVehicle.setVehicleType(VehicleType.LUXURY);
        luxuryVehicle.setVehicleModel("Mercedes S-Class");
        luxuryVehicle.setLicensePlate(licensePlate2);
        luxuryVehicle.setSeats(4);
        luxuryVehicle.setBabyTransport(true);
        luxuryVehicle.setPetTransport(true);
        luxuryVehicle = vehicleRepository.save(luxuryVehicle);

        // Setup active driver
        activeDriver = new Driver();
        activeDriver.setEmail(activeEmail);
        activeDriver.setPassword("password");
        activeDriver.setFirstName("Active");
        activeDriver.setLastName("Driver");
        activeDriver.setPhone("0601234567");
        activeDriver.setAddress("Address 1");
        activeDriver.setUserRole(rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole.DRIVER);
        activeDriver.setAccountActivated(true);
        activeDriver.setAccountBlocked(false);
        activeDriver.setActive(true);
        activeDriver.setVehicle(standardVehicle);
        activeDriver.setActiveMinutesLast24h(100);
        activeDriver.setTotalRides(5);
        activeDriver = driverRepository.save(activeDriver);

        // Setup inactive driver
        inactiveDriver = new Driver();
        inactiveDriver.setEmail(inactiveEmail);
        inactiveDriver.setPassword("password");
        inactiveDriver.setFirstName("Inactive");
        inactiveDriver.setLastName("Driver");
        inactiveDriver.setPhone("0609876543");
        inactiveDriver.setAddress("Address 2");
        inactiveDriver.setUserRole(rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole.DRIVER);
        inactiveDriver.setAccountActivated(true);
        inactiveDriver.setAccountBlocked(false);
        inactiveDriver.setActive(false);
        inactiveDriver.setVehicle(luxuryVehicle);
        inactiveDriver.setActiveMinutesLast24h(50);
        inactiveDriver.setTotalRides(3);
        inactiveDriver = driverRepository.save(inactiveDriver);

        // Setup test passenger (for ride creator)
        testPassenger = new Passenger();
        testPassenger.setEmail(passengerEmail);
        testPassenger.setPassword("password");
        testPassenger.setFirstName("Test");
        testPassenger.setLastName("Passenger");
        testPassenger.setPhone("0605555555");
        testPassenger.setAddress("Passenger Address");
        testPassenger.setUserRole(rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole.PASSENGER);
        testPassenger.setAccountActivated(true);
        testPassenger.setAccountBlocked(false);
        testPassenger = passengerRepository.save(testPassenger);
    }

    @Test
    @DisplayName("findByActive should return only active drivers")
    void testFindByActive_ReturnsOnlyActiveDrivers() {
        // When
        List<Driver> activeDrivers = driverRepository.findByActive(true);

        // Then
        assertNotNull(activeDrivers);
        assertFalse(activeDrivers.isEmpty());
        // All returned drivers must be active
        assertTrue(activeDrivers.stream().allMatch(Driver::isActive));
        // Must include our test active driver
        assertTrue(activeDrivers.stream().anyMatch(d -> d.getEmail().startsWith("active")));
    }

    @Test
    @DisplayName("findByActive should return only inactive drivers when false")
    void testFindByActive_ReturnsOnlyInactiveDrivers() {
        // When
        List<Driver> inactiveDrivers = driverRepository.findByActive(false);

        // Then
        assertNotNull(inactiveDrivers);
        assertFalse(inactiveDrivers.isEmpty());
        // All returned drivers must be inactive
        assertTrue(inactiveDrivers.stream().allMatch(d -> !d.isActive()));
        // Must include our test inactive driver
        assertTrue(inactiveDrivers.stream().anyMatch(d -> d.getEmail().startsWith("inactive")));
    }

    @Test
    @DisplayName("findAllWithoutRideStatus should return drivers without specified ride status")
    void testFindAllWithoutRideStatus_ReturnsDriversWithoutStatus() {
        // Given - Create a ride with STARTED status for active driver
        Route route = new Route();
        route.setStartLocation("Location A");
        route.setEndLocation("Location B");
        route.setStartLatitude(45.25);
        route.setStartLongitude(19.83);
        route.setEndLatitude(45.26);
        route.setEndLongitude(19.84);
        route = routeRepository.save(route);

        Ride ride = new Ride();
        ride.setDriver(activeDriver);
        ride.setRoute(route);
        ride.setStatus(RideStatus.STARTED);
        ride.setDate(LocalDate.now());
        ride.setStartLocation("Location A");
        ride.setEndLocation("Location B");
        ride.setPrice(500.0);
        ride.setCreator(testPassenger);
        ride = rideRepository.save(ride);

        // When
        List<Driver> driversWithoutStarted = driverRepository.findAllWithoutRideStatus(RideStatus.STARTED);

        // Then - Should not include active driver who has STARTED ride
        assertNotNull(driversWithoutStarted);
        assertTrue(driversWithoutStarted.stream().noneMatch(d -> d.getId().equals(activeDriver.getId())));
    }

    @Test
    @DisplayName("findAllWithoutStartedRide should return drivers without STARTED rides")
    void testFindAllWithoutStartedRide_ExcludesDriversWithStartedRides() {
        // Given - Create STARTED ride
        Route route = new Route();
        route.setStartLocation("Location A");
        route.setEndLocation("Location B");
        route.setStartLatitude(45.25);
        route.setStartLongitude(19.83);
        route.setEndLatitude(45.26);
        route.setEndLongitude(19.84);
        route = routeRepository.save(route);

        Ride startedRide = new Ride();
        startedRide.setDriver(activeDriver);
        startedRide.setRoute(route);
        startedRide.setStatus(RideStatus.STARTED);
        startedRide.setDate(LocalDate.now());
        startedRide.setStartLocation("Location A");
        startedRide.setEndLocation("Location B");
        startedRide.setPrice(500.0);
        startedRide.setCreator(testPassenger);
        startedRide = rideRepository.save(startedRide);

        // When
        List<Driver> drivers = driverRepository.findAllWithoutStartedRide(RideStatus.STARTED);

        // Then
        assertNotNull(drivers);
        assertTrue(drivers.stream().noneMatch(d -> d.getId().equals(activeDriver.getId())));
    }

    @Test
    @DisplayName("findAllWithStartedButNoCreated should return drivers with STARTED but no CREATED rides")
    void testFindAllWithStartedButNoCreated_ReturnsCorrectDrivers() {
        // Given - Active driver has STARTED ride
        Route route1 = new Route();
        route1.setStartLocation("Location A");
        route1.setEndLocation("Location B");
        route1.setStartLatitude(45.25);
        route1.setStartLongitude(19.83);
        route1.setEndLatitude(45.26);
        route1.setEndLongitude(19.84);
        route1 = routeRepository.save(route1);

        Ride startedRide = new Ride();
        startedRide.setDriver(activeDriver);
        startedRide.setRoute(route1);
        startedRide.setStatus(RideStatus.STARTED);
        startedRide.setDate(LocalDate.now());
        startedRide.setStartLocation("Location A");
        startedRide.setEndLocation("Location B");
        startedRide.setPrice(500.0);
        startedRide.setCreator(testPassenger);
        startedRide = rideRepository.save(startedRide);

        // Inactive driver has CREATED ride
        Route route2 = new Route();
        route2.setStartLocation("Location C");
        route2.setEndLocation("Location D");
        route2.setStartLatitude(45.27);
        route2.setStartLongitude(19.85);
        route2.setEndLatitude(45.28);
        route2.setEndLongitude(19.86);
        route2 = routeRepository.save(route2);

        Ride createdRide = new Ride();
        createdRide.setDriver(inactiveDriver);
        createdRide.setRoute(route2);
        createdRide.setStatus(RideStatus.CREATED);
        createdRide.setDate(LocalDate.now());
        createdRide.setStartLocation("Location C");
        createdRide.setEndLocation("Location D");
        createdRide.setPrice(600.0);
        createdRide.setCreator(testPassenger);
        createdRide = rideRepository.save(createdRide);

        // When
        List<Driver> drivers = driverRepository.findAllWithStartedButNoCreated(
                RideStatus.STARTED, RideStatus.CREATED);

        // Then - Should include active driver (has STARTED, no CREATED)
        assertNotNull(drivers);
        assertTrue(drivers.stream().anyMatch(d -> d.getId().equals(activeDriver.getId())));
        // Should NOT include inactive driver (has CREATED)
        assertTrue(drivers.stream().noneMatch(d -> d.getId().equals(inactiveDriver.getId())));
    }

    @Test
    @DisplayName("findAvailableDrivers should return drivers with neither STARTED nor CREATED rides")
    void testFindAvailableDrivers_ReturnsOnlyFullyAvailableDrivers() {
        // Given - Create another driver with no rides
        long timestamp = System.currentTimeMillis() % 100000;
        String vanLicense = "BG-" + (200 + (timestamp % 700)) + "-CC";
        String availableDriverEmail = "available" + timestamp + "@test.com";

        Vehicle van = new Vehicle();
        van.setVehicleType(VehicleType.VAN);
        van.setVehicleModel("Mercedes Vito");
        van.setLicensePlate(vanLicense);
        van.setSeats(7);
        van.setBabyTransport(false);
        van.setPetTransport(false);
        van = vehicleRepository.save(van);

        Driver availableDriver = new Driver();
        availableDriver.setEmail(availableDriverEmail);
        availableDriver.setPassword("password");
        availableDriver.setFirstName("Available");
        availableDriver.setLastName("Driver");
        availableDriver.setPhone("0641111111");
        availableDriver.setAddress("Address 3");
        availableDriver.setUserRole(rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole.DRIVER);
        availableDriver.setAccountActivated(true);
        availableDriver.setAccountBlocked(false);
        availableDriver.setActive(true);
        availableDriver.setVehicle(van);
        availableDriver.setActiveMinutesLast24h(0);
        availableDriver.setTotalRides(0);
        final Driver savedAvailableDriver = driverRepository.save(availableDriver);

        // Active driver has STARTED ride
        Route route = new Route();
        route.setStartLocation("Location A");
        route.setEndLocation("Location B");
        route.setStartLatitude(45.25);
        route.setStartLongitude(19.83);
        route.setEndLatitude(45.26);
        route.setEndLongitude(19.84);
        route = routeRepository.save(route);

        Ride ride = new Ride();
        ride.setDriver(activeDriver);
        ride.setRoute(route);
        ride.setStatus(RideStatus.STARTED);
        ride.setDate(LocalDate.now());
        ride.setStartLocation("Location A");
        ride.setEndLocation("Location B");
        ride.setPrice(500.0);
        ride.setCreator(testPassenger);
        ride = rideRepository.save(ride);

        // When
        List<Driver> drivers = driverRepository.findAvailableDrivers(
                RideStatus.STARTED, false, false);

        // Then - Should only include driver with no rides
        assertNotNull(drivers);
        assertTrue(drivers.stream().anyMatch(d -> d.getId().equals(savedAvailableDriver.getId())));
        assertTrue(drivers.stream().noneMatch(d -> d.getId().equals(activeDriver.getId())));
    }

    @Test
    @DisplayName("Custom queries should handle null vehicle gracefully")
    void testCustomQueries_HandleNullVehicle() {
        // Given - Driver without vehicle
        long timestamp = System.currentTimeMillis() % 100000;
        String noVehicleEmail = "novehicle" + timestamp + "@test.com";

        Driver driverWithoutVehicle = new Driver();
        driverWithoutVehicle.setEmail(noVehicleEmail);
        driverWithoutVehicle.setPassword("password");
        driverWithoutVehicle.setFirstName("NoVehicle");
        driverWithoutVehicle.setLastName("Driver");
        driverWithoutVehicle.setPhone("0642222222");
        driverWithoutVehicle.setAddress("Address 4");
        driverWithoutVehicle.setUserRole(rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole.DRIVER);
        driverWithoutVehicle.setAccountActivated(true);
        driverWithoutVehicle.setAccountBlocked(false);
        driverWithoutVehicle.setActive(true);
        driverWithoutVehicle.setVehicle(null);
        driverWithoutVehicle.setActiveMinutesLast24h(0);
        driverWithoutVehicle.setTotalRides(0);
        driverWithoutVehicle = driverRepository.save(driverWithoutVehicle);

        // When
        List<Driver> activeDrivers = driverRepository.findByActive(true);
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(
                RideStatus.STARTED, false, false);

        // Then - Should not throw exception
        assertNotNull(activeDrivers);
        assertNotNull(availableDrivers);
        assertTrue(activeDrivers.size() >= 1);
    }
}
