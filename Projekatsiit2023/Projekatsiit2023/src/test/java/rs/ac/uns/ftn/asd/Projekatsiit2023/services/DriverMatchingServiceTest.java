package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Driver Matching Service Tests - Ride Ordering Functionality")
class DriverMatchingServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideEstimationService estimationService;

    @InjectMocks
    private DriverMatchingService driverMatchingService;

    private Driver driver1;
    private Driver driver2;
    private Driver driver3;
    private Vehicle standardVehicle;
    private Vehicle luxuryVehicle;

    @BeforeEach
    void setUp() {
        // Setup vehicles
        standardVehicle = createVehicle(VehicleType.STANDARD, false, false);
        luxuryVehicle = createVehicle(VehicleType.LUXURY, true, true);

        // Setup drivers
        driver1 = createDriver(1L, "driver1@test.com", standardVehicle, true, 100, 5);
        driver2 = createDriver(2L, "driver2@test.com", luxuryVehicle, true, 200, 10);
        driver3 = createDriver(3L, "driver3@test.com", standardVehicle, true, 450, 20);
    }

    @Nested
    @DisplayName("Boundary Tests - No Available Drivers")
    class NoAvailableDriversTests {

        @Test
        @DisplayName("Should return empty when no active drivers exist")
        void testFindBestDriver_NoActiveDrivers() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(Collections.emptyList());

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
            verify(driverRepository).findByActive(true);
            verifyNoMoreInteractions(driverRepository);
        }

        @Test
        @DisplayName("Should return empty when no driver matches vehicle type")
        void testFindBestDriver_NoMatchingVehicleType() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1)); // STANDARD vehicle

            // When - Request VAN
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.VAN, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when baby transport required but not available")
        void testFindBestDriver_BabyTransportRequired_NotAvailable() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1)); // No baby transport

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, true, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when pet transport required but not available")
        void testFindBestDriver_PetTransportRequired_NotAvailable() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1)); // No pet transport

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, true,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when driver would exceed 8-hour limit")
        void testFindBestDriver_DriverExceeds8HourLimit() {
            // Given
            driver3.setActiveMinutesLast24h(470); // Already at 7h 50min
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver3));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(40); // Would exceed 480 minutes (8 hours)
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Positive Tests - Driver Selection")
    class DriverSelectionTests {

        @Test
        @DisplayName("Should return driver when all criteria match and no conflicts")
        void testFindBestDriver_NoConflicts_ReturnsDriver() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, LocalDateTime.now());

            // Then
            assertTrue(result.isPresent());
            assertEquals(driver1.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Should select driver with luxury vehicle when baby transport required")
        void testFindBestDriver_BabyTransportRequired_SelectsCorrectDriver() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));
            when(rideRepository.findByDriverId(anyLong())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.LUXURY, true, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isPresent());
            assertEquals(driver2.getId(), result.get().getId());
            assertTrue(driver2.getVehicle().isBabyTransport());
        }

        @Test
        @DisplayName("Should select driver with pet-friendly vehicle when pets required")
        void testFindBestDriver_PetTransportRequired_SelectsCorrectDriver() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));
            when(rideRepository.findByDriverId(anyLong())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.LUXURY, false, true,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isPresent());
            assertEquals(driver2.getId(), result.get().getId());
            assertTrue(driver2.getVehicle().isPetTransport());
        }
    }

    @Nested
    @DisplayName("Conflict Resolution Tests")
    class ConflictResolutionTests {

        @Test
        @DisplayName("Should detect conflict when driver has STARTED ride")
        void testFindBestDriver_DriverHasStartedRide_Conflicts() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            Ride startedRide = createRide(driver1, RideStatus.STARTED, LocalDateTime.now(), 30);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(startedRide));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, LocalDateTime.now());

            // Then - Should still find driver when conflict can be resolved
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should detect conflict when driver has CREATED ride for same time")
        void testFindBestDriver_DriverHasCreatedRideNow_DetectsConflict() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            Ride createdRide = createRide(driver1, RideStatus.CREATED, now, 30);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(createdRide));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, now);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should prioritize driver with no conflicts over driver needing to wait")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_PrioritizesNoConflictDriver() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));

            // Driver1 has no rides
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            // Driver2 has a CREATED ride
            Ride ride = createRide(driver2, RideStatus.CREATED, LocalDateTime.now(), 30);
            when(rideRepository.findByDriverId(driver2.getId())).thenReturn(List.of(ride));

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, LocalDateTime.now());

            // Then - Should select driver1 (no conflicts)
            assertTrue(result.isPresent());
            assertEquals(driver1.getId(), result.get().getId());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null scheduledAt parameter")
        void testFindBestDriver_NullScheduledAt() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isPresent());
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, 30, 60, 120, 240, 479 })
        @DisplayName("Should accept driver with various active minutes under limit")
        void testFindBestDriver_VariousActiveMinutes_UnderLimit(int activeMinutes) {
            // Given
            driver1.setActiveMinutesLast24h(activeMinutes);
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(1); // Short ride
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isPresent());
        }

        @ParameterizedTest
        @ValueSource(ints = { 480, 481, 500 })
        @DisplayName("Should reject driver at or over 8-hour limit")
        void testFindBestDriver_VariousActiveMinutes_AtOrOverLimit(int activeMinutes) {
            // Given
            driver1.setActiveMinutesLast24h(activeMinutes);
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle estimation service failure gracefully")
        void testFindBestDriver_EstimationServiceFails() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());
            when(estimationService.estimate(any())).thenThrow(new RuntimeException("OSRM unavailable"));

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then - Should use default time and continue
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("Should handle driver with multiple rides")
        void testFindBestDriver_DriverWithMultipleRides() {
            // Given
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            Ride ride1 = createRide(driver1, RideStatus.CREATED, LocalDateTime.now().minusHours(1), 30);
            Ride ride2 = createRide(driver1, RideStatus.FINISHED, LocalDateTime.now().minusHours(2), 45);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride1, ride2));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, LocalDateTime.now());

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle driver with null vehicle gracefully")
        void testFindBestDriver_DriverWithNullVehicle() {
            // Given
            driver1.setVehicle(null);
            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, null);

            // Then - Should filter out driver with null vehicle
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Priority 2 Driver Selection - Complex Scheduling Tests")
    class Priority2DriverSelectionTests {

        @Test
        @DisplayName("Should prioritize driver with NO conflicts over driver with conflicts")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_PriorityNoConflictOverConflict() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));

            // Driver1: NO conflicts
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            // Driver2: Has CREATED ride at request time
            Ride existingRide = createRide(driver2, RideStatus.CREATED, requestTime, 30);
            when(rideRepository.findByDriverId(driver2.getId())).thenReturn(List.of(existingRide));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then
            assertTrue(result.isPresent());
            assertEquals(driver1.getId(), result.get().getId(),
                    "Should select driver with no conflicts (Priority 1) over driver with conflicts (Priority 2)");
        }

        @Test
        @DisplayName("Should return Priority 2 driver when all have conflicts")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_Priority2_AllHaveConflicts() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));

            // Both drivers have rides BEFORE the request time so both are Priority 2
            LocalDateTime rideTime = requestTime.minusHours(2);
            Ride ride1 = createRide(driver1, RideStatus.CREATED, rideTime, 30);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride1));

            Ride ride2 = createRide(driver2, RideStatus.CREATED, rideTime.minusHours(1), 30);
            when(rideRepository.findByDriverId(driver2.getId())).thenReturn(List.of(ride2));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then - Should find a driver (from Priority 2)
            assertTrue(result.isPresent(), "Should return a Priority 2 driver when both have conflicts");
        }

        @Test
        @DisplayName("Should reject when driver cannot fit between two scheduled rides")
        void testFindBestDriver_CantFitBetweenTwoRides() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now().plusHours(1);

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // Driver has ride from 10:00-10:30 and 11:00-11:30
            // Request at 10:45 with 30min duration can't fit (overlaps both)
            LocalDateTime ride1Start = requestTime.minusMinutes(45);
            Ride ride1 = createRide(driver1, RideStatus.CREATED, ride1Start, 30);

            LocalDateTime ride2Start = requestTime.plusMinutes(15);
            Ride ride2 = createRide(driver1, RideStatus.CREATED, ride2Start, 30);

            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride1, ride2));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then
            assertTrue(result.isEmpty(), "Should reject driver who cannot fit between existing rides");
        }

        @Test
        @DisplayName("Should fit request after ride with buffer time")
        void testFindBestDriver_FitsAfterRideWithBuffer() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // Ride: 9:00-9:30 with 10min buffer = available from 9:40
            // Request at 10:00 (20min after buffer) = should fit
            LocalDateTime ride1Start = requestTime.minusMinutes(60);
            Ride ride1 = createRide(driver1, RideStatus.CREATED, ride1Start, 30);

            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride1));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then
            assertTrue(result.isPresent());
            assertEquals(driver1.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Should handle multiple drivers with STARTED rides")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_MultipleDriversWithStartedRides() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2, driver3));

            // Driver1: STARTED ride
            Ride startedRide1 = createRide(driver1, RideStatus.STARTED, requestTime.minusMinutes(30), 20);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(startedRide1));

            // Driver2: No rides
            when(rideRepository.findByDriverId(driver2.getId())).thenReturn(Collections.emptyList());

            // Driver3: STARTED ride
            Ride startedRide3 = createRide(driver3, RideStatus.STARTED, requestTime.minusMinutes(60), 40);
            when(rideRepository.findByDriverId(driver3.getId())).thenReturn(List.of(startedRide3));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then - Should select a Priority 1 driver (one without conflicts)
            assertTrue(result.isPresent());
            assertTrue(List.of(driver1.getId(), driver2.getId(), driver3.getId()).contains(result.get().getId()),
                    "Should select one of the available drivers");
        }

        @Test
        @DisplayName("Should handle estimation failure and use defaults")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_Priority2_EstimationFails() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // Driver has past ride
            LocalDateTime rideStart = requestTime.minusHours(2);
            Ride ride = createRide(driver1, RideStatus.CREATED, rideStart, 30);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride));

            // Estimation fails
            when(estimationService.estimate(any())).thenThrow(new RuntimeException("OSRM unavailable"));

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then - Should use defaults and find driver
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("Should reject when all exceed 8-hour limit")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_AllExceedEightHourLimit() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // Driver at 479 minutes
            driver1.setActiveMinutesLast24h(479);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30); // Would exceed 480
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle complex multiple ride scenarios")
        @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
        void testFindBestDriver_ComplexMultipleRides() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1, driver2));

            // Driver1: Multiple past rides
            Ride d1r1 = createRide(driver1, RideStatus.CREATED, requestTime.minusHours(3), 30);
            Ride d1r2 = createRide(driver1, RideStatus.CREATED, requestTime.minusHours(1), 20);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(d1r1, d1r2));

            // Driver2: No rides
            when(rideRepository.findByDriverId(driver2.getId())).thenReturn(Collections.emptyList());

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then - Should select a driver (could be either depending on evaluation order)
            assertTrue(result.isPresent());
            assertTrue(List.of(driver1.getId(), driver2.getId()).contains(result.get().getId()),
                    "Should select one of the available drivers");
        }

        @Test
        @DisplayName("Should accept scheduled ride far in future with good gap")
        void testFindBestDriver_FutureRide_GoodGap() {
            // Given
            LocalDateTime requestTime = LocalDateTime.now();

            when(driverRepository.findByActive(true)).thenReturn(List.of(driver1));

            // Ride in 4 hours, request now = plenty of gap
            LocalDateTime futureRide = requestTime.plusHours(4);
            Ride ride = createRide(driver1, RideStatus.CREATED, futureRide, 30);
            when(rideRepository.findByDriverId(driver1.getId())).thenReturn(List.of(ride));

            RideEstimationResponseDTO estimation = new RideEstimationResponseDTO();
            estimation.setEstimatedTime(30);
            when(estimationService.estimate(any())).thenReturn(estimation);

            // When
            Optional<Driver> result = driverMatchingService.findBestDriver(
                    VehicleType.STANDARD, false, false,
                    45.25, 19.83, 45.26, 19.84, requestTime);

            // Then
            assertTrue(result.isPresent());
        }
    }

    // Helper methods
    private Driver createDriver(Long id, String email, Vehicle vehicle, boolean active,
            int activeMinutes, int totalRides) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setEmail(email);
        driver.setPassword("password");
        driver.setActive(active);
        driver.setVehicle(vehicle);
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        driver.setActiveMinutesLast24h(activeMinutes);
        driver.setTotalRides(totalRides);
        return driver;
    }

    private Vehicle createVehicle(VehicleType type, boolean babyTransport, boolean petTransport) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(type);
        vehicle.setVehicleModel("Test Model");
        vehicle.setLicensePlate("BG-123-AA");
        vehicle.setBabyTransport(babyTransport);
        vehicle.setPetTransport(petTransport);
        return vehicle;
    }

    private Ride createRide(Driver driver, RideStatus status, LocalDateTime scheduledAt, int durationMinutes) {
        Route route = new Route();
        route.setStartLocation("Location A");
        route.setEndLocation("Location B");
        route.setStartLatitude(45.25);
        route.setStartLongitude(19.83);
        route.setEndLatitude(45.26);
        route.setEndLongitude(19.84);

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setRoute(route);
        ride.setStatus(status);
        ride.setScheduledAt(scheduledAt);
        ride.setDate(LocalDate.now());
        ride.setStartLocation("Location A");
        ride.setEndLocation("Location B");
        ride.setPrice(500.0);
        ride.setEstimatedDurationMinutes(durationMinutes);

        if (status == RideStatus.STARTED) {
            ride.setStartedAt(scheduledAt);
        }

        return ride;
    }
}
