package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;

@RestController
@RequestMapping("/api/ride-estimation")
public class RideEstimationController {

    @PostMapping
    public ResponseEntity<RideEstimationResponseDTO> estimateRide(
            @RequestBody RideEstimationRequestDTO estimationRequest) {
        RideEstimationResponseDTO response = new RideEstimationResponseDTO();
        response.setStartLocation(estimationRequest.getStartLocation());
        response.setEndLocation(estimationRequest.getEndLocation());
        response.setEstimatedDistance(12.5);
        response.setEstimatedTime(25);
        response.setEstimatedPrice(1620.0); // cena_po_tipu_vozila + km * 120
        response.setRoute("Dummy route coordinates");
        return ResponseEntity.ok(response);
    }
}