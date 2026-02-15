package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import org.springframework.security.test.context.support.WithMockUser;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "driver@test.com", roles = {"DRIVER"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RideControllerFinishIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Ride startedRide;
    private Driver driver;
    private Passenger passenger;

    @BeforeEach
    void setUp() {

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel("Skoda Octavia");
        vehicle.setVehicleType(VehicleType.STANDARD);
        vehicle.setLicensePlate("NS-721-LU");
        vehicle.setSeats(4);

        driver = new Driver();
        driver.setEmail("driverFinishRide@test.com");
        driver.setPassword("pass");
        driver.setFirstName("Lazar");
        driver.setLastName("Lazic");
        driver.setAddress("Address 1");
        driver.setPhone("060111222");
        driver.setUserRole(UserRole.DRIVER);
        driver.setAccountActivated(true);
        driver.setAccountBlocked(false);
        driver.setActive(false);
        driver.setVehicle(vehicle);
        driver = driverRepository.save(driver);

        passenger = new Passenger();
        passenger.setEmail("passengerFinishRide@test.com");
        passenger.setPassword("pass");
        passenger.setFirstName("Ana");
        passenger.setLastName("Anic");
        passenger.setAddress("Address 2");
        passenger.setPhone("061222333");
        passenger.setUserRole(UserRole.PASSENGER);
        passenger.setAccountActivated(true);
        passenger.setAccountBlocked(false);
        passenger.setStartedRide(true);
        passenger = passengerRepository.save(passenger);

        Route route = new Route();
        route.setStartLocation("A");
        route.setEndLocation("B");
        route.setStartLatitude(45.0);
        route.setStartLongitude(19.0);
        route.setEndLatitude(45.1);
        route.setEndLongitude(19.1);
        route = routeRepository.save(route);

        startedRide = new Ride();
        startedRide.setDriver(driver);
        startedRide.setRoute(route);
        startedRide.setCreator(passenger);
        startedRide.setPassengers(List.of(passenger));
        startedRide.setStartLocation("A");
        startedRide.setDate(LocalDate.now());
        startedRide.setStatus(RideStatus.STARTED);
        startedRide.setPaid(false);
        startedRide.setTotalDistance(1.1);
        startedRide.setPanicTriggered(false);
        startedRide.setRideRated(false);
        startedRide.setDriverReported(false);
        startedRide.setRideStopped(false);
        startedRide = rideRepository.save(startedRide);
    }

    @Test
    @DisplayName("Should finish ride successfully and return 200")
    void shouldFinishRideSuccessfully() throws Exception {

        mockMvc.perform(post("/api/rides/{rideId}/finish", startedRide.getId()))
                .andExpect(status().isOk());

        Ride updatedRide = rideRepository.findById(startedRide.getId()).orElseThrow();

        assertEquals(RideStatus.FINISHED, updatedRide.getStatus());
        assertTrue(updatedRide.isPaid());

        Passenger updatedPassenger = passengerRepository.findById(passenger.getId()).orElseThrow();
        assertFalse(updatedPassenger.isStartedRide());

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertTrue(updatedDriver.isActive());
    }
}
