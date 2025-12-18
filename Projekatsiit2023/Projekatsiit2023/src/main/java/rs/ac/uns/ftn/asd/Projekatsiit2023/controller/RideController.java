package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @PostMapping("/estimate")
    public ResponseEntity<RideEstimateResponseDTO> estimateRide(
            @RequestBody RideEstimateRequestDTO request) {

        RideEstimateResponseDTO response = new RideEstimateResponseDTO();
        response.estimatedTimeMinutes = 12;
        response.estimatedPrice = 780;

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RideDTO> createRide() {
        RideDTO dto = new RideDTO();
        dto.id = 1L;
        dto.status = "CREATED";
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelRide(@PathVariable Long id) {
        return ResponseEntity.ok("Ride cancelled");
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startRide(@PathVariable Long id) {
        return ResponseEntity.ok("Ride started");
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<String> finishRide(@PathVariable Long id) {
        return ResponseEntity.ok("Ride finished");
    }
}
