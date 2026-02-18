package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideOrderRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.NotificationRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

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
@DisplayName("Ride Controller Order Integration Tests - 2.4.1 Poručivanje vozila")
@WithMockUser(username = "passenger@test.com", roles = { "PASSENGER" })
class RideControllerOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private ObjectMapper objectMapper;
    private Driver driver1;
    private Driver driver2;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        userRepository.deleteAll();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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

        // Create driver1 with standard vehicle
        Vehicle standardVehicle = new Vehicle();
        standardVehicle.setVehicleModel("Toyota Corolla");
        standardVehicle.setVehicleType(VehicleType.STANDARD);
        standardVehicle.setLicensePlate("NS-001-AB1");
        standardVehicle.setSeats(4);
        standardVehicle.setBabyTransport(false);
        standardVehicle.setPetTransport(false);

        driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword("password123");
        driver1.setFirstName("Marko");
        driver1.setLastName("Markovic");
        driver1.setAddress("Futoski put 2");
        driver1.setUserRole(UserRole.DRIVER);
        driver1.setPhone("0621234567");
        driver1.setAccountActivated(true);
        driver1.setAccountBlocked(false);
        driver1.setActive(true);
        driver1.setActiveMinutesLast24h(100);
        driver1.setTotalRides(10);
        driver1.setRating(4.5);
        driver1.setVehicle(standardVehicle);
        driver1 = driverRepository.save(driver1);

        // Create driver2 with luxury vehicle
        Vehicle luxuryVehicle = new Vehicle();
        luxuryVehicle.setVehicleModel("Mercedes S-Class");
        luxuryVehicle.setVehicleType(VehicleType.LUXURY);
        luxuryVehicle.setLicensePlate("BG-999-XX");
        luxuryVehicle.setSeats(4);
        luxuryVehicle.setBabyTransport(true);
        luxuryVehicle.setPetTransport(true);

        driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword("password123");
        driver2.setFirstName("Petar");
        driver2.setLastName("Petrovic");
        driver2.setUserRole(UserRole.DRIVER);
        driver2.setAddress("Bulevar Oslobodjenja 100");
        driver2.setPhone("0631234567");
        driver2.setAccountActivated(true);
        driver2.setAccountBlocked(false);
        driver2.setActive(true);
        driver2.setActiveMinutesLast24h(200);
        driver2.setTotalRides(25);
        driver2.setRating(4.8);
        driver2.setVehicle(luxuryVehicle);
        driver2 = driverRepository.save(driver2);
    }

    // Helper method
    private RideOrderRequestDTO createValidRideRequest() {
        RideOrderRequestDTO request = new RideOrderRequestDTO();
        request.setCreatorId(passenger.getId());
        request.setStartLocation("Bulevar Oslobodjenja 46, Novi Sad");
        request.setEndLocation("Futoska 10, Novi Sad");
        request.setStartLatitude(45.2524);
        request.setStartLongitude(19.8350);
        request.setEndLatitude(45.2600);
        request.setEndLongitude(19.8400);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabySeat(false);
        request.setPetFriendly(false);
        request.setEstimatedPrice(500.0);
        return request;
    }

    @Nested
    @DisplayName("Positive Test Cases - Successful Ride Orders")
    class SuccessfulRideOrderTests {

        @Test
        @DisplayName("Should successfully order immediate ride with standard vehicle")
        void testOrderRide_ImmediateRide_Success() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setScheduledAt(null); // Immediate ride
            request.setVehicleType(VehicleType.STANDARD);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rideId").exists())
                    .andExpect(jsonPath("$.driverId").value(driver1.getId()))
                    .andExpect(jsonPath("$.status").value("CREATED"));

            Ride savedRide = rideRepository.findAll().get(0);
            assertEquals(RideStatus.CREATED, savedRide.getStatus());
            assertEquals(driver1.getId(), savedRide.getDriver().getId());
            assertEquals(passenger.getId(), savedRide.getCreator().getId());
        }

        @Test
        @DisplayName("Should assign luxury vehicle when baby transport required")
        void testOrderRide_BabyTransportRequired_AssignsCorrectVehicle() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setVehicleType(VehicleType.LUXURY);
            request.setBabySeat(true);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.driverId").value(driver2.getId()));

            Ride savedRide = rideRepository.findAll().get(0);
            assertTrue(savedRide.getDriver().getVehicle().isBabyTransport());
        }
    }

    @Nested
    @DisplayName("Boundary and Exceptional Test Cases")
    class BoundaryTestCases {

        @Test
        @DisplayName("Should fail when no drivers available")
        void testOrderRide_NoDriversAvailable_Returns503() throws Exception {
            driver1.setActive(false);
            driver2.setActive(false);
            driverRepository.save(driver1);
            driverRepository.save(driver2);

            RideOrderRequestDTO request = createValidRideRequest();

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().string(containsString("No drivers currently available")));

            assertEquals(0, rideRepository.count());
        }

        @Test
        @DisplayName("Should fail with missing creator ID")
        void testOrderRide_MissingCreatorId_ReturnsBadRequest() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setCreatorId(null);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Additional Edge and Validation Test Cases")
    class AdditionalEdgeCases {

        @Test
        @DisplayName("Should successfully order a scheduled ride")
        void testOrderRide_ScheduledRide_Success() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setScheduledAt(LocalDateTime.now().plusDays(1)); // Scheduled for tomorrow

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rideId").exists())
                    .andExpect(jsonPath("$.status").value("CREATED"));
        }

        @Test
        @DisplayName("Should fail with null start location")
        void testOrderRide_NullStartLocation_ReturnsBadRequest() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setStartLocation(null);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid coordinates")
        void testOrderRide_InvalidCoordinates_ReturnsBadRequest() throws Exception {
            RideOrderRequestDTO request = createValidRideRequest();
            request.setStartLatitude(999.0); // Invalid latitude

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should assign pet-friendly driver when pet-friendly requested")
        void testOrderRide_PetFriendly_AssignsCorrectDriver() throws Exception {
            // Make driver2's vehicle pet-friendly
            Vehicle petVehicle = driver2.getVehicle();
            petVehicle.setPetTransport(true);
            driver2.setVehicle(petVehicle);
            driverRepository.save(driver2);

            RideOrderRequestDTO request = createValidRideRequest();
            request.setVehicleType(VehicleType.LUXURY);
            request.setPetFriendly(true);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.driverId").value(driver2.getId()));

            Ride savedRide = rideRepository.findAll().get(0);
            assertTrue(savedRide.getDriver().getVehicle().isPetTransport());
        }

        @Test
        @DisplayName("Should notify all passengers including creator")
        void testOrderRide_MultiplePassengers_Notification() throws Exception {
            // Add a second passenger
            final Passenger passenger2 = new Passenger();
            passenger2.setEmail("passenger2@test.com");
            passenger2.setPassword("password123");
            passenger2.setFirstName("Jovana");
            passenger2.setLastName("Jovic");
            passenger2.setUserRole(UserRole.PASSENGER);
            passenger2.setAddress("Bulevar Oslobodjenja 50");
            passenger2.setPhone("0617654321");
            passenger2.setAccountActivated(true);
            passenger2.setAccountBlocked(false);
            passengerRepository.save(passenger2);

            RideOrderRequestDTO request = createValidRideRequest();
            List<Long> passengerIds = new ArrayList<>();
            passengerIds.add(passenger.getId());
            passengerIds.add(passenger2.getId());
            request.setPassengerIds(passengerIds);

            mockMvc.perform(post("/api/rides")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Check that a notification was created for both the creator and the second
            // passenger
            boolean notifiedCreator = notificationRepository.findAll().stream()
                    .anyMatch(n -> n.getRecipient().getId().equals(passenger.getId()));
            boolean notifiedSecond = notificationRepository.findAll().stream()
                    .anyMatch(n -> n.getRecipient().getId().equals(passenger2.getId()));
            assertTrue(notifiedCreator, "Creator should be notified");
            assertTrue(notifiedSecond, "Second passenger should be notified");
        }

    }

}
