package rs.ac.uns.ftn.asd.Projekatsiit2023.controller.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.StopRideResponseDTO;

@RestController
@RequestMapping("/api/rides")
public class RideStopController {

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
}