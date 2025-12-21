package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideRatingDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.RideTrackingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.model.Ride;

@RestController
@RequestMapping("/api/rides")
public class RideController {

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

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<String> rateRide(@PathVariable Long rideId, @RequestBody RideRatingDTO rideRatingDTO) {
        return ResponseEntity.ok("Ride with ID " + rideId + " has been rated.");
    }
}
