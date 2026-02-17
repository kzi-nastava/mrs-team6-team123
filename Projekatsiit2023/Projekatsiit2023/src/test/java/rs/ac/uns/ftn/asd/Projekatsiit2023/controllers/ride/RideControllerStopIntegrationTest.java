package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "driver@test.com", roles = {"DRIVER"})
@Transactional
class RideControllerStopIntegrationTest {

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

    private ObjectMapper objectMapper;
    private Ride startedRide;
    private Driver driver;
    private Passenger passenger;
    private Route route;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel("Toyota Prius");
        vehicle.setVehicleType(VehicleType.STANDARD);
        vehicle.setLicensePlate("NS-123-AP");
        vehicle.setSeats(4);
        vehicle.setBabyTransport(false);
        vehicle.setPetTransport(false);

        // Create and save driver
        driver = new Driver();
        driver.setEmail("driver@test.com");
        driver.setPassword("password123");
        driver.setFirstName("Marko");
        driver.setLastName("Markovic");
        driver.setAddress("Bulevar Oslobodjenja 1");
        driver.setPhone("0601234567");
        driver.setProfileImage("");
        driver.setUserRole(UserRole.DRIVER);
        driver.setAccountActivated(true);
        driver.setAccountBlocked(false);
        driver.setActive(true);
        driver.setActiveMinutesLast24h(0);
        driver.setTotalRides(5);
        driver.setRating(4.5);
        driver.setVehicle(vehicle);
        driver = driverRepository.save(driver);

        // Create and save passenger
        passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword("password123");
        passenger.setFirstName("Ana");
        passenger.setLastName("Anic");
        passenger.setAddress("Strazilovska 10");
        passenger.setPhone("0611111111");
        passenger.setProfileImage("");
        passenger.setUserRole(UserRole.PASSENGER);
        passenger.setAccountActivated(true);
        passenger.setAccountBlocked(false);
        passenger.setStartedRide(true);
        passenger = passengerRepository.save(passenger);

        // Create and save route
        route = new Route();
        route.setStartLocation("45.2511,19.8367");
        route.setEndLocation("45.2671,19.8335");
        route.setStartLatitude(45.2511);
        route.setStartLongitude(19.8367);
        route.setEndLatitude(45.2671);
        route.setEndLongitude(19.8335);
        route = routeRepository.save(route);

        // Create and save a STARTED ride
        startedRide = new Ride();
        startedRide.setDriver(driver);
        startedRide.setCreator(passenger);
        startedRide.setRoute(route);
        startedRide.setStartLocation("45.2511,19.8367");
        startedRide.setEndLocation("45.2671,19.8335");
        startedRide.setDate(LocalDate.now());
        startedRide.setStatus(RideStatus.STARTED);
        startedRide.setPrice(800.0);
        startedRide.setTotalDistance(5.0);
        startedRide.setPanicTriggered(false);
        startedRide.setRideRated(false);
        startedRide.setDriverReported(false);
        startedRide.setRideStopped(false);
        startedRide = rideRepository.save(startedRide);
    }

    private StopRideRequestDTO createValidRequest() {
        StopRideRequestDTO req = new StopRideRequestDTO();
        req.setCurrentLocation("45.2600,19.8350");
        req.setStoppedAt(LocalDateTime.of(2025, 6, 15, 14, 30));
        return req;
    }

    // ==================== HAPPY PATH ====================

    @Nested
    @DisplayName("Happy path - POST /api/rides/{rideId}/stop")
    class HappyPathTests {

        @Test
        @DisplayName("Should stop ride and return 200 with response body")
        void shouldStopRideSuccessfully() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rideId").value(startedRide.getId()))
                    .andExpect(jsonPath("$.stoppedLocation").value("45.2600,19.8350"))
                    .andExpect(jsonPath("$.recalculatedPrice").isNumber())
                    .andExpect(jsonPath("$.message", containsString("Ride stopped successfully")));
        }

        @Test
        @DisplayName("Should persist ride status as FINISHED in database")
        void shouldPersistFinishedStatus() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            Ride updatedRide = rideRepository.findById(startedRide.getId()).orElseThrow();
            assertEquals(RideStatus.FINISHED, updatedRide.getStatus());
            assertTrue(updatedRide.isRideStopped());
        }

        @Test
        @DisplayName("Should update route end coordinates in database")
        void shouldUpdateRouteInDatabase() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            Route updatedRoute = routeRepository.findById(route.getId()).orElseThrow();
            assertEquals(45.26, updatedRoute.getEndLatitude(), 0.01);
            assertEquals(19.835, updatedRoute.getEndLongitude(), 0.01);
        }
    }

    // ==================== RIDE NOT FOUND ====================

    @Nested
    @DisplayName("Ride not found tests")
    class RideNotFoundTests {

        @Test
        @DisplayName("Should return 400 when ride does not exist")
        void shouldReturn400WhenRideNotFound() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Ride not found")));
        }
    }

    // ==================== INVALID RIDE STATUS ====================

    @Nested
    @DisplayName("Invalid ride status tests")
    class InvalidStatusTests {

        @Test
        @DisplayName("Should return 400 when ride status is CREATED")
        void shouldReturn400WhenCreated() throws Exception {
            startedRide.setStatus(RideStatus.CREATED);
            rideRepository.save(startedRide);

            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Can only stop a ride that is in progress")));
        }

        @Test
        @DisplayName("Should return 400 when ride status is FINISHED")
        void shouldReturn400WhenFinished() throws Exception {
            startedRide.setStatus(RideStatus.FINISHED);
            rideRepository.save(startedRide);

            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when ride status is CANCELLED")
        void shouldReturn400WhenCancelled() throws Exception {
            startedRide.setStatus(RideStatus.CANCELLED);
            rideRepository.save(startedRide);

            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== ALREADY STOPPED ====================

    @Nested
    @DisplayName("Already stopped tests")
    class AlreadyStoppedTests {

        @Test
        @DisplayName("Should return 400 when ride is already stopped")
        void shouldReturn400WhenAlreadyStopped() throws Exception {
            startedRide.setRideStopped(true);
            rideRepository.save(startedRide);

            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Ride has already been stopped")));
        }

        @Test
        @DisplayName("Should return 400 when stopping same ride twice")
        void shouldReturn400WhenStoppingTwice() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            // First stop - should succeed
            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            // Second stop - should fail
            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== INVALID REQUEST BODY ====================

    @Nested
    @DisplayName("Invalid request body tests")
    class InvalidRequestTests {

        @Test
        @DisplayName("Should return 400 when currentLocation has invalid format")
        void shouldReturn400WhenInvalidLocationFormat() throws Exception {
            StopRideRequestDTO req = new StopRideRequestDTO();
            req.setCurrentLocation("invalid-location");
            req.setStoppedAt(LocalDateTime.now());

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when coordinates are out of range")
        void shouldReturn400WhenCoordsOutOfRange() throws Exception {
            StopRideRequestDTO req = new StopRideRequestDTO();
            req.setCurrentLocation("91.0,19.8367");
            req.setStoppedAt(LocalDateTime.now());

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when request body is empty")
        void shouldReturn400WhenEmptyBody() throws Exception {
            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when request body is malformed JSON")
        void shouldReturn400WhenMalformedJson() throws Exception {
            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when currentLocation is empty string")
        void shouldReturn400WhenEmptyLocation() throws Exception {
            StopRideRequestDTO req = new StopRideRequestDTO();
            req.setCurrentLocation("");
            req.setStoppedAt(LocalDateTime.now());

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== HTTP METHOD TESTS ====================

    @Nested
    @DisplayName("HTTP method tests")
    class HttpMethodTests {

        @Test
        @DisplayName("Should return 405 when using GET instead of POST")
        void shouldReturn405WhenGet() throws Exception {
            mockMvc.perform(get("/api/rides/{rideId}/stop", startedRide.getId()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should return 405 when using PUT instead of POST")
        void shouldReturn405WhenPut() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(put("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should return 405 when using DELETE instead of POST")
        void shouldReturn405WhenDelete() throws Exception {
            mockMvc.perform(delete("/api/rides/{rideId}/stop", startedRide.getId()))
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    // ==================== PRICE RECALCULATION (end-to-end check) ====================

    @Nested
    @DisplayName("Price recalculation integration tests")
    class PriceRecalculationTests {

        @Test
        @DisplayName("Recalculated price should be less than original when stopped early")
        void priceShouldBeLessThanOriginal() throws Exception {
            double originalPrice = startedRide.getPrice();

            StopRideRequestDTO req = new StopRideRequestDTO();
            req.setCurrentLocation("45.2530,19.8360"); // close to start
            req.setStoppedAt(LocalDateTime.now());

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recalculatedPrice", lessThan(originalPrice)));
        }

        @Test
        @DisplayName("Recalculated price should be positive")
        void priceShouldBePositive() throws Exception {
            StopRideRequestDTO req = createValidRequest();

            mockMvc.perform(post("/api/rides/{rideId}/stop", startedRide.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recalculatedPrice", greaterThanOrEqualTo(0.0)));
        }
    }
}