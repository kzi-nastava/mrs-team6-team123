package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.FinishRideService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RateRideService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.TrackRideService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideCancellationService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideStopService;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final TrackRideService trackRideService;
    private final RideCancellationService cancellationService;
    private final FinishRideService finishRideService;
    private final RateRideService rateRideService;
    private final RideStopService rideStopService;

    public RideController(
             RideCancellationService cancellationService,
             TrackRideService trackRideService,
             FinishRideService finishRideService,
             RateRideService rateRideService,
             RideStopService rideStopService) {
        this.cancellationService = cancellationService;
        this.trackRideService = trackRideService;
        this.finishRideService = finishRideService;
        this.rateRideService = rateRideService;
        this.rideStopService = rideStopService;
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
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long rideId) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(rideId);
        response.setDriverId(42L);
        response.setStatus(RideStatus.STARTED);
        return ResponseEntity.ok(response);
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