package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.TrackRideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final TrackRideService trackRideService;

    public RideController(TrackRideService trackRideService) {
        this.trackRideService = trackRideService;
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
    public ResponseEntity<?> trackRide(@PathVariable Long rideId) {
        try {
            RideTrackingResponseDTO response = trackRideService.trackRide(rideId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
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
    public ResponseEntity<CancelRideResponseDTO> cancelRide(
            @PathVariable Long rideId,
            @RequestBody CancelRideRequestDTO cancelRequest) {
        // TODO: Validate if ride can be cancelled (10 min before start for passengers)
        // TODO: Check who is cancelling (driver or passenger)
        // TODO: Send notifications to all parties
        CancelRideResponseDTO response = new CancelRideResponseDTO();
        response.setRideId(rideId);
        response.setCancelledBy(cancelRequest.getUserId());
        response.setReason(cancelRequest.getReason());
        response.setMessage("Ride successfully cancelled.");
        return ResponseEntity.ok(response);
    }
}
