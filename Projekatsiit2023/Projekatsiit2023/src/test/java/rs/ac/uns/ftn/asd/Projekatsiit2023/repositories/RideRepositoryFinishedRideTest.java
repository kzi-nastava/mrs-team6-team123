package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.*;

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
@DisplayName("Ride Repository Tests - Finished Rides")
public class RideRepositoryFinishedRideTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    private Driver driver1;
    private Driver driver2;
    private Passenger passenger1;
    private Passenger passenger2;
    private Route route;
    private Ride ride1;
    private Ride ride2;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
        passengerRepository.deleteAll();
        driverRepository.deleteAll();
        routeRepository.deleteAll();

        // Create drivers
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleModel("Peugeot 308");
        vehicle1.setVehicleType(VehicleType.STANDARD);
        vehicle1.setLicensePlate("NS-749-NP");
        vehicle1.setSeats(4);
        vehicle1.setBabyTransport(false);
        vehicle1.setPetTransport(false);

        driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword("password123");
        driver1.setFirstName("Tara");
        driver1.setLastName("Taric");
        driver1.setAddress("Futoski put 2");
        driver1.setUserRole(UserRole.DRIVER);
        driver1.setPhone("0621234567");
        driver1.setAccountActivated(true);
        driver1.setAccountBlocked(false);
        driver1.setActive(true);
        driver1.setActiveMinutesLast24h(100);
        driver1.setTotalRides(10);
        driver1.setRating(4.5);
        driver1.setVehicle(vehicle1);
        driver1 = driverRepository.save(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleModel("Skoda Octavia");
        vehicle2.setVehicleType(VehicleType.STANDARD);
        vehicle2.setLicensePlate("NS-198-LA");
        vehicle2.setSeats(4);
        vehicle2.setBabyTransport(false);
        vehicle2.setPetTransport(false);

        driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword("password123");
        driver2.setFirstName("Pera");
        driver2.setLastName("Peric");
        driver2.setAddress("Heroja Pinkija 2");
        driver2.setUserRole(UserRole.DRIVER);
        driver2.setPhone("0609876543");
        driver2.setAccountActivated(true);
        driver2.setAccountBlocked(false);
        driver2.setActive(true);
        driver2.setActiveMinutesLast24h(100);
        driver2.setTotalRides(10);
        driver2.setRating(4.5);
        driver2.setVehicle(vehicle2);
        driver2 = driverRepository.save(driver2);

        // Create passenger
        passenger1 = new Passenger();
        passenger1.setEmail("passenger1@test.com");
        passenger1.setPassword("password123");
        passenger1.setFirstName("Ana");
        passenger1.setLastName("Anic");
        passenger1.setAddress("Bulevar Cara Lazara 1");
        passenger1.setPhone("0611234567");
        passenger1.setUserRole(UserRole.PASSENGER);
        passenger1.setAccountActivated(true);
        passenger1.setAccountBlocked(false);
        passenger1 = passengerRepository.save(passenger1);

        passenger2 = new Passenger();
        passenger2.setEmail("passenger2@test.com");
        passenger2.setPassword("password123");
        passenger2.setFirstName("Mima");
        passenger2.setLastName("Mimic");
        passenger2.setAddress("Temerinska 1");
        passenger2.setPhone("0691472583");
        passenger2.setUserRole(UserRole.PASSENGER);
        passenger2.setAccountActivated(true);
        passenger2.setAccountBlocked(false);
        passenger2 = passengerRepository.save(passenger2);

        // Create route
        route = new Route();
        route.setStartLocation("A");
        route.setEndLocation("B");
        route.setStartLatitude(45.2671);
        route.setStartLongitude(19.8335);
        route.setEndLatitude(45.2550);
        route.setEndLongitude(19.8450);
        route = routeRepository.save(route);

        // Create ride
        ride1 = new Ride();
        ride1.setDriver(driver1);
        ride1.setCreator(passenger1);
        ride1.setPassengers(List.of(passenger1));
        ride1.setStartLocation(route.getStartLocation());
        ride1.setDate(LocalDate.now());
        ride1.setPrice(500);
        ride1.setTotalDistance(10);
        ride1.setPaid(false);
        ride1.setRideStopped(false);
        ride1.setPanicTriggered(false);
        ride1.setRoute(route);
        ride1.setStatus(RideStatus.FINISHED);
        ride1.setRideRated(false);
        ride1.setDriverReported(false);
        ride1 = rideRepository.save(ride1);

        ride2 = new Ride();
        ride2.setDriver(driver2);
        ride2.setCreator(passenger1);
        ride2.setPassengers(List.of(passenger1));
        ride2.setStartLocation(route.getStartLocation());
        ride2.setDate(LocalDate.now());
        ride2.setPrice(500);
        ride2.setTotalDistance(10);
        ride2.setPaid(false);
        ride2.setRideStopped(false);
        ride2.setPanicTriggered(false);
        ride2.setRoute(route);
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setRideRated(false);
        ride2.setDriverReported(false);
        ride2 = rideRepository.save(ride2);
    }

    @Test
    @DisplayName("Test Find Finished Rides By Passenger ID")
    void testFindFinishedRidesByPassengerId() {
        // When
        List<Ride> finishedRides = rideRepository.findFinishedRidesByPassengerId(passenger1.getId());

        // Then
        assertNotNull(finishedRides);
        assertFalse(finishedRides.isEmpty());
        assertEquals(2, finishedRides.size());

        // All returned rides must be FINISHED and must include passenger1
        for (Ride ride : finishedRides) {
            assertEquals(RideStatus.FINISHED, ride.getStatus());
            assertTrue(ride.getPassengers().stream()
                    .anyMatch(p -> p.getId().equals(passenger1.getId())));
        }
    }

    @Test
    @DisplayName("Test Find Finished Rides By Driver ID")
    void testFindFinishedRidesByDriverId() {
        // When
        List<Ride> finishedRides = rideRepository.findFinishedRidesByDriverId(driver1.getId());

        // Then
        assertNotNull(finishedRides);
        assertFalse(finishedRides.isEmpty());
        assertEquals(1, finishedRides.size());

        // All returned rides must be FINISHED and must include driver1
        for (Ride ride : finishedRides) {
            assertEquals(RideStatus.FINISHED, ride.getStatus());
            assertEquals(ride.getDriver().getId(), driver1.getId());
        }
    }

    @Test
    @DisplayName("Test Find All Finished Rides")
    void testFindAllFinishedRides() {
        // When
        List<Ride> finishedRides = rideRepository.findAllFinishedRides();

        // Then
        assertNotNull(finishedRides);
        assertFalse(finishedRides.isEmpty());
        assertEquals(2, finishedRides.size());

        // All returned rides must be FINISHED
        for (Ride ride : finishedRides) {
            assertEquals(RideStatus.FINISHED, ride.getStatus());
        }
    }

    @Test
    @DisplayName("Test Find Finished Rides By Passenger ID - No Rides")
    void testFindFinishedRidesByPassengerIdNoRides() {
        // When
        List<Ride> finishedRides = rideRepository.findFinishedRidesByPassengerId(passenger2.getId());

        // Then
        assertNotNull(finishedRides);
        assertTrue(finishedRides.isEmpty());
    }

    @Test
    @DisplayName("Test Find Finished Rides By Driver ID - Non Existing Driver")
    void testFindFinishedRidesByDriverIdNoRides() {
        // When
        List<Ride> finishedRides = rideRepository.findFinishedRidesByDriverId(999L);

        // Then
        assertNotNull(finishedRides);
        assertTrue(finishedRides.isEmpty());
    }

}
