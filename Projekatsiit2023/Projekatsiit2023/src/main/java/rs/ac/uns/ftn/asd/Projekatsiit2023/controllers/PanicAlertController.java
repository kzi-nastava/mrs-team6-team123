package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PanicAlert;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.PanicAlertService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/panic")
public class PanicAlertController {

    private final PanicAlertService panicAlertService;

    public PanicAlertController(PanicAlertService panicAlertService) {
        this.panicAlertService = panicAlertService;
    }

    // 2.6.3 PANIC dugme
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerPanic(@RequestBody Map<String, Object> request) {
        try {
            Long rideId = Long.valueOf(request.get("rideId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            String currentLocation = request.get("currentLocation").toString();

            PanicAlert alert = panicAlertService.triggerPanic(rideId, userId, currentLocation);
            
            return ResponseEntity.ok(Map.of(
                "message", "Panic alert triggered successfully. Admins have been notified.",
                "alertId", alert.getId(),
                "triggeredAt", alert.getTriggeredAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping("/alerts/unresolved")
    public ResponseEntity<List<PanicAlert>> getUnresolvedAlerts() {
        return ResponseEntity.ok(panicAlertService.getAllUnresolvedAlerts());
    }

    @GetMapping("/alerts/all")
    public ResponseEntity<List<PanicAlert>> getAllAlerts() {
        return ResponseEntity.ok(panicAlertService.getAllAlerts());
    }

    @PostMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> request) {
        try {
            Long adminId = Long.valueOf(request.get("adminId").toString());
            String notes = request.get("notes").toString();

            PanicAlert alert = panicAlertService.resolveAlert(alertId, adminId, notes);
            
            return ResponseEntity.ok(Map.of(
                "message", "Alert resolved successfully",
                "alert", alert
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}