package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideOrderRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideRatingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideTrackingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideResponseDTO;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    // 2.4.1 Poručivanje vožnje
    @PostMapping
    public ResponseEntity<RideResponseDTO> orderRide(@RequestBody RideOrderRequestDTO request,
            @RequestParam(required = false) Boolean immediate) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(500L);
        response.setDriverId(42L);
        response.setStatus("CREATED");
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
        response.setStatus("CREATED");
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
        response.setStatus("FINISHED");
        return ResponseEntity.ok(response);
    }

    // 2.6.1 Početak vožnje
    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long rideId) {
        RideResponseDTO response = new RideResponseDTO();
        response.setRideId(rideId);
        response.setDriverId(42L);
        response.setStatus("STARTED");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<RideRatingResponseDTO> rateRide(@PathVariable Long rideId) {
        RideRatingResponseDTO response = new RideRatingResponseDTO();
        response.setRideId(rideId);
        response.setDriverId(20L);
        response.setVehicleId(21L);
        response.setDriverRating(9);
        response.setVehicleRating(8);
        return ResponseEntity.ok(response);
    }
}
