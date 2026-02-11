package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverAssignedRideDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.IrregularityReport;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.IrregularityReportService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.DriverService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.LinkedPassengersService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.DriversRideValidation;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {
    private final IrregularityReportService reportService;
    private final DriverService driverService;
    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final LinkedPassengersService linkedPassengersService;
    private final DriversRideValidation assignRideValidation;

    public DriverController(
            DriverService driverService,
            IrregularityReportService reportService,
            RideRepository rideRepository,
            PassengerRepository passengerRepository,
            LinkedPassengersService linkedPassengersService,
            DriversRideValidation assignRideValidation) {
        this.driverService = driverService;
        this.reportService = reportService;
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.linkedPassengersService = linkedPassengersService;
        this.assignRideValidation = assignRideValidation;
    }

    // 2.2.3 Registracija vozača
    @PostMapping
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody DriverRegistrationRequestDTO request) {
        DriverResponseDTO response = driverService.registerDriver(request);
        return ResponseEntity.created(URI.create("/api/drivers/" + response.getId()))
                .body(response);
    }

    @PostMapping({ "/report" })
    public ResponseEntity<?> reportDriver(@RequestBody ReportDriverRequestDTO request) {
        try {
            reportService.reportDriver(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }

    // Get assigned rides for driver (not finished, started, or canceled)
    @GetMapping("/{driverId}/assigned-rides")
    public ResponseEntity<?> getAssignedRides(@PathVariable Long driverId) {
        try {
            // Fetch only active rides from database
            List<RideStatus> activeStatuses = List.of(RideStatus.CREATED, RideStatus.STARTED);
            List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(driverId, activeStatuses);

            // Map to DTOs
            List<DriverAssignedRideDTO> assignedRides = rides.stream()
                    .map(ride -> {
                        DriverAssignedRideDTO dto = new DriverAssignedRideDTO();
                        dto.setRideId(ride.getId());
                        dto.setStartLocation(ride.getStartLocation());
                        dto.setEndLocation(ride.getEndLocation());
                        dto.setStartLatitude(ride.getRoute().getStartLatitude());
                        dto.setStartLongitude(ride.getRoute().getStartLongitude());
                        dto.setEndLatitude(ride.getRoute().getEndLatitude());
                        dto.setEndLongitude(ride.getRoute().getEndLongitude());
                        dto.setStatus(ride.getStatus());
                        dto.setScheduledAt(ride.getScheduledAt());
                        dto.setEstimatedPrice(ride.getPrice());
                        dto.setPassengerNames(ride.getPassengers().stream()
                                .map(p -> p.getFirstName() + " " + p.getLastName())
                                .collect(Collectors.toList()));
                        dto.setVehicleType(ride.getDriver().getVehicle().getVehicleType().toString());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(assignedRides);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching assigned rides: " + e.getMessage());
        }
    }

    // Accept a ride
    // Start a ride
    @PostMapping("/{driverId}/rides/{rideId}/start")
    public ResponseEntity<?> startRide(@PathVariable Long driverId, @PathVariable Long rideId) {
        try {
            Ride ride = assignRideValidation.validateRideExists(rideId);
            assignRideValidation.validateRideAssignedToDriver(driverId, ride);
            assignRideValidation.validateRideStatusForStart(ride);

            // Set ride status to STARTED and update actual start time
            ride.setStatus(RideStatus.STARTED);
            ride.setStartedAt(java.time.LocalDateTime.now());

            // Set all passengers' startedRide flag to true and send notifications
            for (Passenger passenger : ride.getPassengers()) {
                passenger.setStartedRide(true);
                passengerRepository.save(passenger);
                // Send notification to passenger with ride details and encouragement
                linkedPassengersService.sendNotification(passenger, ride);
            }

            rideRepository.save(ride);

            return ResponseEntity.ok().body("Ride started successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting ride: " + e.getMessage());
        }
    }

}
