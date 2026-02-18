package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StopRideResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideStopServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RideStopService rideStopService;

    private Ride ride;
    private Route route;
    private StopRideRequestDTO request;

    private static final double PRICE_PER_KM = 120.0;

    @BeforeEach
    void setUp() {
        // Setup a default valid route
        route = new Route();
        route.setId(1L);
        route.setStartLocation("45.2511,19.8367");
        route.setEndLocation("45.2671,19.8335");
        route.setStartLatitude(45.2511);
        route.setStartLongitude(19.8367);
        route.setEndLatitude(45.2671);
        route.setEndLongitude(19.8335);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setActive(true);

        ride = new Ride();
        ride.setId(1L);
        ride.setDriver(driver);
        ride.setRoute(route);
        ride.setStatus(RideStatus.STARTED);
        ride.setRideStopped(false);
        ride.setStartLocation("45.2511,19.8367");
        ride.setEndLocation("45.2671,19.8335");
        ride.setDate(LocalDate.now());
        ride.setTotalDistance(5.0);
        ride.setPrice(800.0);
        ride.setPanicTriggered(false);
        ride.setRideRated(false);
        ride.setDriverReported(false);

        request = new StopRideRequestDTO();
        request.setCurrentLocation("45.2600,19.8350");
        request.setStoppedAt(LocalDateTime.of(2025, 6, 15, 14, 30));
    }

    // ==================== HAPPY PATH ====================

    @Nested
    @DisplayName("Happy path tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should stop ride successfully with valid data")
        void shouldStopRideSuccessfully() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertNotNull(response);
            assertEquals(1L, response.getRideId());
            assertEquals("45.2600,19.8350", response.getStoppedLocation());
            assertNotNull(response.getStoppedAt());
            assertTrue(response.getRecalculatedPrice() > 0);
            assertTrue(response.getMessage().contains("Ride stopped successfully"));
        }

        @Test
        @DisplayName("Should set ride status to FINISHED after stopping")
        void shouldSetStatusToFinished() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
            verify(rideRepository).save(rideCaptor.capture());
            Ride savedRide = rideCaptor.getValue();

            assertEquals(RideStatus.FINISHED, savedRide.getStatus());
            assertTrue(savedRide.isRideStopped());
        }

        @Test
        @DisplayName("Should update end location on ride and route")
        void shouldUpdateEndLocation() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            ArgumentCaptor<Route> routeCaptor = ArgumentCaptor.forClass(Route.class);
            verify(routeRepository).save(routeCaptor.capture());
            Route savedRoute = routeCaptor.getValue();

            assertEquals("45.2600,19.8350", savedRoute.getEndLocation());
            assertEquals(45.26, savedRoute.getEndLatitude(), 0.01);
            assertEquals(19.835, savedRoute.getEndLongitude(), 0.01);

            assertEquals("45.2600,19.8350", ride.getEndLocation());
        }

        @Test
        @DisplayName("Should save both ride and route")
        void shouldSaveBothRideAndRoute() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            verify(rideRepository, times(1)).save(any(Ride.class));
            verify(routeRepository, times(1)).save(any(Route.class));
        }

        @Test
        @DisplayName("Should use provided stoppedAt time")
        void shouldUseProvidedStoppedAt() {
            LocalDateTime stoppedTime = LocalDateTime.of(2025, 6, 15, 14, 30);
            request.setStoppedAt(stoppedTime);

            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            assertEquals(stoppedTime.toLocalTime(), ride.getEndedAt());
        }

        @Test
        @DisplayName("Should use LocalTime.now() when stoppedAt is null")
        void shouldUseCurrentTimeWhenStoppedAtIsNull() {
            request.setStoppedAt(null);

            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            assertNotNull(ride.getEndedAt());
        }
    }

    // ==================== RIDE NOT FOUND ====================

    @Nested
    @DisplayName("Ride not found tests")
    class RideNotFoundTests {

        @Test
        @DisplayName("Should throw exception when ride does not exist")
        void shouldThrowWhenRideNotFound() {
            when(rideRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(999L, request));

            assertEquals("Ride not found", ex.getMessage());
            verify(rideRepository, never()).save(any());
            verify(routeRepository, never()).save(any());
        }
    }

    // ==================== INVALID RIDE STATUS ====================

    @Nested
    @DisplayName("Invalid ride status tests")
    class InvalidRideStatusTests {

        @Test
        @DisplayName("Should throw when ride status is CREATED")
        void shouldThrowWhenStatusCreated() {
            ride.setStatus(RideStatus.CREATED);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(1L, request));

            assertTrue(ex.getMessage().contains("Can only stop a ride that is in progress"));
            verify(rideRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when ride status is FINISHED")
        void shouldThrowWhenStatusFinished() {
            ride.setStatus(RideStatus.FINISHED);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(1L, request));

            assertTrue(ex.getMessage().contains("Can only stop a ride that is in progress"));
        }

        @Test
        @DisplayName("Should throw when ride status is CANCELLED")
        void shouldThrowWhenStatusCancelled() {
            ride.setStatus(RideStatus.CANCELLED);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(1L, request));

            assertTrue(ex.getMessage().contains("Can only stop a ride that is in progress"));
        }

        @Test
        @DisplayName("Should throw when ride status is ARRIVED")
        void shouldThrowWhenStatusArrived() {
            ride.setStatus(RideStatus.ARRIVED);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(1L, request));

            assertTrue(ex.getMessage().contains("Can only stop a ride that is in progress"));
        }
    }

    // ==================== ALREADY STOPPED ====================

    @Nested
    @DisplayName("Ride already stopped tests")
    class RideAlreadyStoppedTests {

        @Test
        @DisplayName("Should throw when ride has already been stopped")
        void shouldThrowWhenAlreadyStopped() {
            ride.setRideStopped(true);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> rideStopService.stopRide(1L, request));

            assertEquals("Ride has already been stopped", ex.getMessage());
            verify(rideRepository, never()).save(any());
        }
    }

    // ==================== LOCATION PARSING ====================

    @Nested
    @DisplayName("Location parsing tests")
    class LocationParsingTests {

        @Test
        @DisplayName("Should throw when currentLocation is null")
        void shouldThrowWhenLocationNull() {
            request.setCurrentLocation(null);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(Exception.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when currentLocation is empty")
        void shouldThrowWhenLocationEmpty() {
            request.setCurrentLocation("");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(Exception.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when location has no comma separator")
        void shouldThrowWhenNoComma() {
            request.setCurrentLocation("45.2511 19.8367");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when location has too many parts")
        void shouldThrowWhenTooManyParts() {
            request.setCurrentLocation("45.2511,19.8367,100.0");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when location contains non-numeric values")
        void shouldThrowWhenNonNumeric() {
            request.setCurrentLocation("abc,def");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when latitude is out of range (>90)")
        void shouldThrowWhenLatTooHigh() {
            request.setCurrentLocation("91.0,19.8367");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when latitude is out of range (<-90)")
        void shouldThrowWhenLatTooLow() {
            request.setCurrentLocation("-91.0,19.8367");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when longitude is out of range (>180)")
        void shouldThrowWhenLngTooHigh() {
            request.setCurrentLocation("45.2511,181.0");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should throw when longitude is out of range (<-180)")
        void shouldThrowWhenLngTooLow() {
            request.setCurrentLocation("45.2511,-181.0");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

            assertThrows(IllegalArgumentException.class,
                    () -> rideStopService.stopRide(1L, request));
        }

        @ParameterizedTest
        @ValueSource(strings = {"45.2511N,19.8367E", "45.2511S,19.8367W", "45.2511N,19.8367"})
        @DisplayName("Should handle location with NSEW characters")
        void shouldHandleNSEWCharacters(String location) {
            request.setCurrentLocation(location);
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should accept latitude 90")
        void shouldAcceptLatitude90() {
            request.setCurrentLocation("90.0,19.8367");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            assertNotNull(rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should accept latitude -90")
        void shouldAcceptLatitudeNeg90() {
            request.setCurrentLocation("-90.0,19.8367");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            assertNotNull(rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should accept longitude 180")
        void shouldAcceptLongitude180() {
            request.setCurrentLocation("45.2511,180.0");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            assertNotNull(rideStopService.stopRide(1L, request));
        }

        @Test
        @DisplayName("Should accept longitude -180")
        void shouldAcceptLongitudeNeg180() {
            request.setCurrentLocation("45.2511,-180.0");
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            assertNotNull(rideStopService.stopRide(1L, request));
        }
    }

    // ==================== PRICE RECALCULATION ====================

    @Nested
    @DisplayName("Price recalculation tests")
    class PriceRecalculationTests {

        @Test
        @DisplayName("Should recalculate price based on actual distance travelled")
        void shouldRecalculatePrice() {
            request.setCurrentLocation("45.2511,19.8367"); 
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertTrue(response.getRecalculatedPrice() < ride.getPrice() || response.getRecalculatedPrice() <= 200.01);
        }

        @Test
        @DisplayName("Price should be lower when stopped earlier in ride")
        void priceShouldBeLowerWhenStoppedEarly() {
            double originalPrice = ride.getPrice();
            request.setCurrentLocation("45.2530,19.8360"); 
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertTrue(response.getRecalculatedPrice() < originalPrice,
                    "Recalculated price should be less than original for shorter distance");
        }

        @Test
        @DisplayName("Should round price to 2 decimal places")
        void shouldRoundPriceTo2Decimals() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            double price = response.getRecalculatedPrice();
            assertEquals(price, Math.round(price * 100.0) / 100.0, 0.001,
                    "Price should be rounded to 2 decimal places");
        }

        @Test
        @DisplayName("Base price should default to 0 when original price is less than distance * PRICE_PER_KM")
        void basePriceShouldDefaultToZero() {
            ride.setPrice(100.0);        
            ride.setTotalDistance(5.0); 
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertTrue(response.getRecalculatedPrice() >= 0);
        }

        @Test
        @DisplayName("Should update totalDistance on ride to actual distance travelled")
        void shouldUpdateTotalDistance() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
            verify(rideRepository).save(rideCaptor.capture());
            Ride savedRide = rideCaptor.getValue();

            assertNotEquals(5.0, savedRide.getTotalDistance(), 0.01,
                    "Total distance should be updated to actual distance travelled");
            assertTrue(savedRide.getTotalDistance() >= 0);
        }

        @Test
        @DisplayName("Should calculate distance as ~0 when stopped at start location")
        void shouldCalculateZeroDistanceAtStart() {
            request.setCurrentLocation("45.2511,19.8367"); 
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            rideStopService.stopRide(1L, request);

            ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
            verify(rideRepository).save(rideCaptor.capture());

            assertEquals(0.0, rideCaptor.getValue().getTotalDistance(), 0.01);
        }
    }

    // ==================== RESPONSE DTO VALIDATION ====================

    @Nested
    @DisplayName("Response DTO validation tests")
    class ResponseDtoTests {

        @Test
        @DisplayName("Response should contain correct rideId")
        void responseShouldHaveCorrectRideId() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertEquals(1L, response.getRideId());
        }

        @Test
        @DisplayName("Response should contain stopped location from request")
        void responseShouldHaveStoppedLocation() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertEquals(request.getCurrentLocation(), response.getStoppedLocation());
        }

        @Test
        @DisplayName("Response should contain stoppedAt timestamp")
        void responseShouldHaveStoppedAt() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertEquals(request.getStoppedAt(), response.getStoppedAt());
        }

        @Test
        @DisplayName("Response message should contain success indication")
        void responseShouldHaveSuccessMessage() {
            when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenReturn(ride);
            when(routeRepository.save(any(Route.class))).thenReturn(route);

            StopRideResponseDTO response = rideStopService.stopRide(1L, request);

            assertNotNull(response.getMessage());
            assertTrue(response.getMessage().contains("Ride stopped successfully"));
        }
    }
}