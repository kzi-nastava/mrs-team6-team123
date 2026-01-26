package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideEstimationService;

@RestController
@RequestMapping("/api/ride-estimation")
public class RideEstimationController {

    private final RideEstimationService service;

    public RideEstimationController(RideEstimationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RideEstimationResponseDTO> estimateRide(
            @RequestBody RideEstimationRequestDTO estimationRequest) {

        return ResponseEntity.ok(service.estimate(estimationRequest));
    }
}
