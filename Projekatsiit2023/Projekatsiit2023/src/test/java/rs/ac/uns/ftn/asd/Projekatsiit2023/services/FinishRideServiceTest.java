package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FinishRideServiceTest {
    @Mock
    RideRepository rideRepository = mock(RideRepository.class);
    @Mock
    PassengerRepository passengerRepository = mock(PassengerRepository.class);
    @Mock
    DriverRepository driverRepository = mock(DriverRepository.class);
    @Mock
    ActiveVehicleRepository activeVehicleRepository = mock(ActiveVehicleRepository.class);
    @Mock
    NotificationRepository notificationRepository = mock(NotificationRepository.class);
    @Mock
    UserRepository userRepository = mock(UserRepository.class);

    EmailService emailService = new EmailService(new JavaMailSenderImpl());
    NotificationService notificationService = new NotificationService(notificationRepository, userRepository);

    FinishRideService finishRideService = new FinishRideService(
            rideRepository,
            passengerRepository,
            driverRepository,
            activeVehicleRepository,
            emailService,
            notificationService
    );

    private final Long INVALID_RIDE_ID = 0L;
    private final Long VALID_RIDE_ID = 1L;

    @Test
    public void testRideNotFound() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                finishRideService.finishRide(INVALID_RIDE_ID));
        verify(rideRepository).findById(INVALID_RIDE_ID);
        verifyNoInteractions(passengerRepository);
        verifyNoInteractions(driverRepository);
        verifyNoInteractions(activeVehicleRepository);
    }

    @Test
    public void testRideStatusCreated() {
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.CREATED);
        when(rideRepository.findById(VALID_RIDE_ID)).thenReturn(Optional.of(ride));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                finishRideService.finishRide(VALID_RIDE_ID));
        verify(rideRepository).findById(VALID_RIDE_ID);
        verifyNoInteractions(passengerRepository);
        verifyNoInteractions(driverRepository);
        verifyNoInteractions(activeVehicleRepository);
    }

    @Test
    public void testRideStatusFinished() {
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.FINISHED);
        when(rideRepository.findById(VALID_RIDE_ID)).thenReturn(Optional.of(ride));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                finishRideService.finishRide(VALID_RIDE_ID));
        verify(rideRepository).findById(VALID_RIDE_ID);
        verifyNoInteractions(passengerRepository);
        verifyNoInteractions(driverRepository);
        verifyNoInteractions(activeVehicleRepository);
    }

    @Test
    public void testRideStatusCancelled() {
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.CANCELLED);
        when(rideRepository.findById(VALID_RIDE_ID)).thenReturn(Optional.of(ride));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                finishRideService.finishRide(VALID_RIDE_ID));
        verify(rideRepository).findById(VALID_RIDE_ID);
        verifyNoInteractions(passengerRepository);
        verifyNoInteractions(driverRepository);
        verifyNoInteractions(activeVehicleRepository);
    }

    @Test
    public void testSuccessfulFinishRide() {
        // Arrange
        Ride ride = new Ride();
        ride.setStatus(RideStatus.STARTED);
        ride.setPaid(false);

        Route route = new Route();
        route.setEndLocation("Destination");
        route.setEndLatitude(45.0);
        route.setEndLongitude(19.0);
        ride.setRoute(route);

        Driver driver = new Driver();
        driver.setId(10L);
        driver.setActive(false);
        ride.setDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setStartedRide(true);
        ride.setPassengers(List.of(passenger));

        when(rideRepository.findById(VALID_RIDE_ID)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        /*
        doNothing().when(finishRideService).sendNotification(any(), any());
        doNothing().when(finishRideService).sendEmail(any(), any());
        */

        // Act
        finishRideService.finishRide(VALID_RIDE_ID);

        // Assert
        verify(rideRepository).findById(VALID_RIDE_ID);
        verify(rideRepository).save(any(Ride.class));
        verify(passengerRepository).save(passenger);
        verify(driverRepository).save(driver);
        verify(activeVehicleRepository).findByCurrentRideId(VALID_RIDE_ID);

        assertTrue(ride.isPaid());
        assertEquals("Destination", ride.getEndLocation());
        assertEquals(45.0, ride.getEndLatitude());
        assertEquals(19.0, ride.getEndLongitude());
        assertFalse(passenger.isStartedRide());
        assertTrue(driver.isActive());

    }

}
