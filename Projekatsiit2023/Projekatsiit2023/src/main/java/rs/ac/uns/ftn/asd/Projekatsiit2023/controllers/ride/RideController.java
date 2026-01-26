package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideCancellationService;

@RestController
@RequestMapping("/api/rides")
public class RideController {

     private final RideCancellationService cancellationService;

     public RideController(RideCancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }


    // 2.4.1 Poručivanje vožnje
    @PostMapping
    public ResponseEntity<RideResponseDTO> orderRide(@RequestBody RideOrderRequestDTO request,
            @RequestParam(required = false) Boolean immediate) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(500L);
        response.setDriverId(42L);
        response.setStatus(RideStatus.CREATED);
        response.setEstimatedTimeMinutes(8);
        response.setEstimatedPrice(450.0);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<RideTrackingResponseDTO> trackRide(@PathVariable Long rideId) {
        RideTrackingResponseDTO response = new RideTrackingResponseDTO();
        response.setRideId(rideId);
        response.setCurrentLocation("45.2671 N, 19.8335 E");
        response.setNextStop("Boulevard 2");
        response.setTimeLeft(15);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<RideResponseDTO> finishRide(@PathVariable Long rideId) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(rideId);
        response.setDriverId(42L);
        response.setStatus(RideStatus.FINISHED);
        return ResponseEntity.ok(response);
    }

    // 2.6.1 Početak vožnje
    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long rideId) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(rideId);
        response.setDriverId(42L);
        response.setStatus(RideStatus.STARTED);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<RideRatingResponseDTO> rateRide(@RequestBody RideRatingRequestDTO request) {
        RideRatingResponseDTO response = new RideRatingResponseDTO();
        response.setRideId(request.getRideId());
        // driverId and vehicleId will be found using rideId
        response.setDriverId(20L);
        response.setVehicleId(21L);
        response.setDriverRating(request.getDriverRating());
        response.setVehicleRating(request.getVehicleRating());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/stop")
    public ResponseEntity<StopRideResponseDTO> stopRide(
            @PathVariable Long rideId,
            @RequestBody StopRideRequestDTO stopRequest) {
        StopRideResponseDTO response = new StopRideResponseDTO();
        response.setRideId(rideId);
        response.setStoppedLocation(stopRequest.getCurrentLocation());
        response.setStoppedAt(stopRequest.getStoppedAt());
        response.setRecalculatedPrice(850.0);
        response.setMessage("Ride stopped successfully.");
        return ResponseEntity.ok(response);
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