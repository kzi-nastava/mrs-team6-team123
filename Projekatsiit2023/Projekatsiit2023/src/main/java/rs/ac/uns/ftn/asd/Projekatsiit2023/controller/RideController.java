package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideOrderRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideRatingDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideTrackingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.model.Ride;

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
        // dummy data for demonstration
        response.setRideId(rideId);
        response.setCurrentLocation("45.2671 N, 19.8335 E");
        response.setNextStop("Boulevard 2");
        response.setTimeLeft(15);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<String> finishRide(@PathVariable Long rideId) {
        return ResponseEntity.ok("Ride with ID " + rideId + " has been finished.");
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
    public ResponseEntity<String> rateRide(@PathVariable Long rideId, @RequestBody RideRatingDTO rideRatingDTO) {
        return ResponseEntity.ok("Ride with ID " + rideId + " has been rated.");
    }
}
