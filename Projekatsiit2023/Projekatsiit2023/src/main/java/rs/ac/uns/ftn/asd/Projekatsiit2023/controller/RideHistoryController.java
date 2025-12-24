package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.DriverRideHistoryDTO;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/ride-history")
public class RideHistoryController {

    @GetMapping("/{driverId}/rides")
    public ResponseEntity<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @PathVariable Long driverId,
            @RequestParam(required = false)LocalDateTime from,
            @RequestParam(required = false)LocalDateTime to) {
        List<DriverRideHistoryDTO> response = new ArrayList<>();
        response.add(new DriverRideHistoryDTO());
        return ResponseEntity.ok(response);
    }
}
