package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.OrderRideValidation;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "driver@test.com", roles = { "DRIVER" })
@DisplayName("Ride Controller - Finish Ride Integration Tests")
public class RideControllerFinishRideIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    private ObjectMapper objectMapper;
    private Driver driver;
    private Passenger passenger;
    private Route route;
    private Ride ride;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
        routeRepository.deleteAll();
        passengerRepository.deleteAll();
        driverRepository.deleteAll();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create driver
        Vehicle standardVehicle = new Vehicle();
        standardVehicle.setVehicleModel("Peugeot 308");
        standardVehicle.setVehicleType(VehicleType.STANDARD);
        standardVehicle.setLicensePlate("NS-749-NP");
        standardVehicle.setSeats(4);
        standardVehicle.setBabyTransport(false);
        standardVehicle.setPetTransport(false);

        driver = new Driver();
        driver.setEmail("driver1@test.com");
        driver.setPassword("password123");
        driver.setFirstName("Tara");
        driver.setLastName("Taric");
        driver.setAddress("Futoski put 2");
        driver.setUserRole(UserRole.DRIVER);
        driver.setPhone("0621234567");
        driver.setAccountActivated(true);
        driver.setAccountBlocked(false);
        driver.setActive(true);
        driver.setActiveMinutesLast24h(100);
        driver.setTotalRides(10);
        driver.setRating(4.5);
        driver.setVehicle(standardVehicle);
        driver = driverRepository.save(driver);

        // Create passenger
        passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword("password123");
        passenger.setFirstName("Ana");
        passenger.setLastName("Anic");
        passenger.setAddress("Bulevar Cara Lazara 1");
        passenger.setPhone("0611234567");
        passenger.setUserRole(UserRole.PASSENGER);
        passenger.setAccountActivated(true);
        passenger.setAccountBlocked(false);
        passenger = passengerRepository.save(passenger);

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
        ride = new Ride();
        ride.setDriver(driver);
        ride.setCreator(passenger);
        ride.setPassengers(List.of(passenger));
        ride.setStartLocation(route.getStartLocation());
        ride.setDate(LocalDate.now());
        ride.setPrice(500);
        ride.setTotalDistance(10);
        ride.setPaid(false);
        ride.setRideStopped(false);
        ride.setPanicTriggered(false);
        ride.setRoute(route);
        ride.setStatus(RideStatus.STARTED);
        ride.setRideRated(false);
        ride.setDriverReported(false);
        ride = rideRepository.save(ride);
    }

    @Nested
    @DisplayName("Finish Ride - Successful Scenarios")
    class SuccessfulFinishRideTests {
        @Test
        @DisplayName("Should successfully finish a ride with status STARTED")
        public void finishRideTest_StatusStarted_Success() throws Exception {
            Ride ride = rideRepository.findAll().get(0);
            Long rideId = ride.getId();
            ride.setStatus(RideStatus.STARTED);
            rideRepository.save(ride);

            mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                    .andExpect(status().isOk());
            Ride finishedRide = rideRepository.findAll().get(0);
            assertEquals(RideStatus.FINISHED, finishedRide.getStatus());
            assertTrue(finishedRide.isPaid());
        }

        @Test
        @DisplayName("Should successfully finish a ride with status ARRIVED")
        public void finishRideTest_StatusArrived_Success() throws Exception {
            Ride ride = rideRepository.findAll().get(0);
            Long rideId = ride.getId();
            ride.setStatus(RideStatus.ARRIVED);
            rideRepository.save(ride);

            mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                    .andExpect(status().isOk());
            Ride finishedRide = rideRepository.findAll().get(0);
            assertEquals(RideStatus.FINISHED, finishedRide.getStatus());
        }

        @Test
        @DisplayName("Should successfully finish a ride and update related entities")
        public void finishRideTest_UpdateRelatedEntities_Success() throws Exception {
            Ride ride = rideRepository.findAll().get(0);
            Long rideId = ride.getId();
            ride.setStatus(RideStatus.STARTED);
            rideRepository.save(ride);

            mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                    .andExpect(status().isOk());

            Ride finishedRide = rideRepository.findAll().get(0);
            assertEquals(RideStatus.FINISHED, finishedRide.getStatus());
            assertTrue(finishedRide.isPaid());

            Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
            assertTrue(updatedDriver.isActive());

            List<Passenger> updatedPassengers = finishedRide.getPassengers();
            for (Passenger p : updatedPassengers) {
                assertFalse(p.isStartedRide());
            }
        }
    }

    @Nested
    @DisplayName("Finish Ride - Failure Scenarios")
    class FailedFinishRideTests {
        @Test
        @DisplayName("Shouldn't finish a ride with invalid status")
        public void finishRideTest_InvalidStatus_Failure() throws Exception {
            Ride ride = rideRepository.findAll().get(0);
            Long rideId = ride.getId();
            ride.setStatus(RideStatus.FINISHED);
            rideRepository.save(ride);

            mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Shouldn't finish a non-existent ride")
        public void finishRideTest_NonExistentRide_Failure() throws Exception {
            Long invalidRideId = 999L;

            mockMvc.perform(post("/api/rides/{rideId}/finish", invalidRideId))
                    .andExpect(status().isInternalServerError());
        }
    }
}
