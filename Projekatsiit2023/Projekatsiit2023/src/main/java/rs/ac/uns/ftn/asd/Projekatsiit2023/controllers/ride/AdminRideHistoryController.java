// AdminRideHistoryController.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.AdminRideHistoryDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.AdminRideHistoryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/ride-history")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminRideHistoryController {

    private final AdminRideHistoryService service;

    public AdminRideHistoryController(AdminRideHistoryService service) {
        this.service = service;
    }

    /**
     * GET /api/admin/ride-history
     * Vraća SVE vožnje u sistemu (za admina)
     */
    @GetMapping
    public ResponseEntity<?> getAllRideHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        try {
            List<AdminRideHistoryDTO> history = service.getAllRideHistory(from, to, sortBy, sortOrder);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * GET /api/admin/ride-history/user/{userId}
     * Vraća istoriju vožnji za bilo kog korisnika (vozača ili putnika)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRideHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        try {
            List<AdminRideHistoryDTO> history = service.getUserRideHistory(
                    userId, from, to, sortBy, sortOrder);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * GET /api/admin/ride-history/{rideId}
     * Vraća detalje jedne vožnje
     */
    @GetMapping("/{rideId}")
    public ResponseEntity<?> getRideDetails(@PathVariable Long rideId) {
        try {
            AdminRideHistoryDTO ride = service.getRideDetails(rideId);
            return ResponseEntity.ok(ride);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}