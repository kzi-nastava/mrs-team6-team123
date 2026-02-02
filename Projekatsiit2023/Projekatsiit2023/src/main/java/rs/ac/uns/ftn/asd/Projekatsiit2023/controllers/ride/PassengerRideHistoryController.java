// PassengerRideHistoryController.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.PassengerRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.PassengerRideHistoryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/passenger")
@CrossOrigin(origins = "http://localhost:4200")
public class PassengerRideHistoryController {

    private final PassengerRideHistoryService service;

    public PassengerRideHistoryController(PassengerRideHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{passengerId}/rides")
    public ResponseEntity<?> getPassengerRideHistory(
            @PathVariable Long passengerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        try {
            List<PassengerRideHistoryDTO> history = service.getPassengerRideHistory(
                    passengerId, from, to, sortBy, sortOrder);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{passengerId}/rides/{rideId}")
    public ResponseEntity<?> getRideDetails(
            @PathVariable Long passengerId,
            @PathVariable Long rideId) {
        try {
            PassengerRideHistoryDTO ride = service.getRideDetails(rideId, passengerId);
            return ResponseEntity.ok(ride);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}