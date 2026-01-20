package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideHistoryService;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/ride-history")
public class RideHistoryController {

    private final RideHistoryService service;

    public RideHistoryController(RideHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{driverId}/rides")
    public ResponseEntity<?> getDriverRideHistory(
            @PathVariable Long driverId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {
        try {
            List<DriverRideHistoryDTO> response = service.getDriverRideHistory(driverId, from, to);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching driver's ride history.");
        }

    }
}
