package rs.ac.uns.ftn.asd.Projekatsiit2023.controller.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.CancelRideRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.CancelRideResponseDTO;

@RestController
@RequestMapping("/api/rides")
public class RideCancellationController {

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