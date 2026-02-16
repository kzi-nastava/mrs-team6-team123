package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.OrderRideValidation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final TrackRideService trackRideService;
    private final RideCancellationService cancellationService;
    private final FinishRideService finishRideService;
    private final RateRideService rateRideService;
    private final RideStopService rideStopService;
    private final NotificationService notificationService;
    private final DriverMatchingService driverMatchingService;
    private final RideService rideService;
    private final RideEstimationService estimationService;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final RouteRepository routeRepository;
    private final RideRepository rideRepository;
    private final OrderRideValidation orderRideValidation;

    public RideController(
            RideCancellationService cancellationService,
            TrackRideService trackRideService,
            RideStopService rideStopService,
            NotificationService notificationService,
            DriverMatchingService driverMatchingService,
            RideService rideService,
            RideEstimationService estimationService,
            DriverRepository driverRepository,
            PassengerRepository passengerRepository,
            RouteRepository routeRepository,
            RideRepository rideRepository,
            FinishRideService finishRideService,
            RateRideService rateRideService,
            OrderRideValidation orderRideValidation) {
        this.cancellationService = cancellationService;
        this.trackRideService = trackRideService;
        this.rideStopService = rideStopService;
        this.notificationService = notificationService;
        this.driverMatchingService = driverMatchingService;
        this.rideService = rideService;
        this.estimationService = estimationService;
        this.driverRepository = driverRepository;
        this.passengerRepository = passengerRepository;
        this.routeRepository = routeRepository;
        this.rideRepository = rideRepository;
        this.finishRideService = finishRideService;
        this.rateRideService = rateRideService;
        this.orderRideValidation = orderRideValidation;
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/estimate")
    public ResponseEntity<?> estimateRide(@RequestBody RideEstimationRequestDTO request) {
        try {
            RideEstimationResponseDTO estimation = estimationService.estimate(request);
            return ResponseEntity.ok(estimation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2.4.1 Poručivanje vožnje
    @PostMapping
    public ResponseEntity<?> orderRide(@RequestBody RideOrderRequestDTO request) {
        try {
            orderRideValidation.validateOrderRideRequest(request);

            // Check if creator is blocked
            Passenger creator = passengerRepository.findById(request.getCreatorId())
                    .orElseThrow(() -> new RuntimeException("Creator not found"));
            if (creator.isAccountBlocked()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Your account is blocked. You cannot book a ride.");
            }

            // Check if any passenger is blocked
            List<Passenger> passengers = new java.util.ArrayList<>();
            if (request.getPassengerIds() != null && !request.getPassengerIds().isEmpty()) {
                passengers = passengerRepository.findAllById(request.getPassengerIds());
                for (Passenger p : passengers) {
                    if (p.isAccountBlocked()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("One or more passengers are blocked and cannot book a ride.");
                    }
                }
            }

            // Create and save Route with address strings and coordinates
            Route route = new Route();
            route.setStartLocation(request.getStartLocation());
            route.setEndLocation(request.getEndLocation());
            route.setStartLatitude(request.getStartLatitude());
            route.setStartLongitude(request.getStartLongitude());
            route.setEndLatitude(request.getEndLatitude());
            route.setEndLongitude(request.getEndLongitude());
            Route savedRoute = routeRepository.save(route);

            // Find best available driver using coordinates
            Optional<Driver> driverOptional = driverMatchingService.findBestDriver(
                    request.getVehicleType(),
                    request.isBabySeat(),
                    request.isPetFriendly(),
                    request.getStartLatitude(),
                    request.getStartLongitude(),
                    request.getEndLatitude(),
                    request.getEndLongitude(),
                    request.getScheduledAt());

            if (driverOptional.isEmpty()) {
                // No driver available - notify the ride creator
                notificationService.sendNotification(
                        request.getCreatorId(),
                        "No Drivers Available",
                        "We could not find an available driver for your ride. Please try again later.",
                        null);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("No drivers currently available. Please try again later.");
            }

            // Create ride with assigned driver
            Driver driver = driverOptional.get();
            driver.setTotalRides(driver.getTotalRides() + 1);
            driverRepository.save(driver);

            Ride ride = new Ride();
            ride.setDriver(driver);
            ride.setRoute(savedRoute);

            // Set creator
            ride.setCreator(creator);

            // Add all passengers (if any)
            if (request.getPassengerIds() != null && !request.getPassengerIds().isEmpty()) {
                ride.setPassengers(passengerRepository.findAllById(request.getPassengerIds()));
            } else {
                ride.setPassengers(new java.util.ArrayList<>());
            }

            // Set location and time info
            ride.setStartLocation(request.getStartLocation());
            ride.setEndLocation(request.getEndLocation());
            ride.setScheduledAt(request.getScheduledAt());
            ride.setDate(LocalDate.now());
            ride.setStatus(RideStatus.CREATED);
            ride.setPrice(request.getEstimatedPrice() != null ? request.getEstimatedPrice() : 0.0);

            // Calculate actual distance using estimation service
            double distance = 0.0;
            try {
                RideEstimationRequestDTO estimationRequest = new RideEstimationRequestDTO();
                estimationRequest.setStartLocation(request.getStartLatitude() + "," + request.getStartLongitude());
                estimationRequest.setEndLocation(request.getEndLatitude() + "," + request.getEndLongitude());
                estimationRequest.setVehicleType(request.getVehicleType());
                RideEstimationResponseDTO estimation = estimationService.estimate(estimationRequest);
                distance = estimation.getEstimatedDistance();
            } catch (Exception e) {
                System.err.println("Failed to calculate distance: " + e.getMessage());
            }
            ride.setTotalDistance(distance);

            ride.setPanicTriggered(false);
            ride.setRideRated(false);
            ride.setDriverReported(false);
            ride.setRideStopped(false);

            // Save ride to database
            Ride savedRide = rideService.create(ride);

            // Build response
            RideResponseDTO response = new RideResponseDTO();
            response.setRideId(savedRide.getId());
            response.setDriverId(driver.getId());
            response.setDriverName(driver.getFirstName() + " " + driver.getLastName());
            response.setVehicleLicense(driver.getVehicle().getLicensePlate());
            response.setStatus(RideStatus.CREATED);
            response.setEstimatedTimeMinutes(8); // TODO: calculate from route

            // Send success notification to passenger with driver details
            // Send success notification to creator and all passengers with driver details
            String notificationMessage = "Your ride has been booked. Driver: " + response.getDriverName()
                    + " | Vehicle: " + response.getVehicleLicense();

            // Send notification to all passengers (skip creator to avoid duplicate)
            for (Passenger passenger : savedRide.getPassengers()) {
                notificationService.sendNotification(
                        passenger.getId(),
                        "Ride Booked Successfully",
                        notificationMessage,
                        null);

            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to order ride: " + e.getMessage());
        }
    }

    // 2.4.3 Poručivanje vožnje iz omiljenih ruta
    @PostMapping("/favorites/{favoriteRouteId}")
    public ResponseEntity<RideResponseDTO> orderRideFromFavorite(@PathVariable Long favoriteRouteId,
            @RequestParam(required = false) Boolean immediate) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(501L);
        response.setDriverId(43L);
        response.setStatus(RideStatus.CREATED);
        response.setEstimatedTimeMinutes(7);
        response.setEstimatedPrice(420.0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rideId}/tracking")
    public ResponseEntity<?> trackRide(@PathVariable Long rideId) {
        try {
            RideTrackingResponseDTO response = trackRideService.trackRide(rideId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<?> finishRide(@PathVariable Long rideId) {
        try {
            finishRideService.finishRide(rideId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 2.6.1 Početak vožnje
    
@PostMapping("/{rideId}/start")
public ResponseEntity<?> startRide(@PathVariable Long rideId) {
    try {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.CREATED) {
            return ResponseEntity.badRequest()
                    .body("Ride cannot be started — current status: " + ride.getStatus());
        }

        ride.setStartedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.STARTED);
        rideRepository.save(ride);

        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(ride.getId());
        response.setDriverId(ride.getDriver().getId());
        response.setStatus(RideStatus.STARTED);

        return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<?> rateRide(@RequestBody RideRatingResponseDTO response) {
        try {
            rateRideService.rateRide(response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{rideId}/for-rating")
    public ResponseEntity<?> getRideForRating(@PathVariable Long rideId) {
        try {
            RideRatingRequestDTO response = rateRideService.getRideForRating(rideId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/stop")
    public ResponseEntity<?> stopRide(
            @PathVariable Long rideId,
            @RequestBody StopRideRequestDTO stopRequest) {
        try {
            StopRideResponseDTO response = rideStopService.stopRide(rideId, stopRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<?> cancelRide(
            @PathVariable Long rideId,
            @RequestBody CancelRideRequestDTO cancelRequest) {
        try {
            CancelRideResponseDTO response = cancellationService.cancelRide(rideId, cancelRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}