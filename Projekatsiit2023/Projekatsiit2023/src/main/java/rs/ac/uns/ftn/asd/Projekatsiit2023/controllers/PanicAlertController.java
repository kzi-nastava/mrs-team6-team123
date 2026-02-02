// PanicAlertController.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.PanicAlertRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.PanicAlertResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.ResolvePanicRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PanicAlert;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.PanicAlertService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/panic")
@CrossOrigin(origins = "http://localhost:4200")
public class PanicAlertController {

    private final PanicAlertService panicAlertService;

    public PanicAlertController(PanicAlertService panicAlertService) {
        this.panicAlertService = panicAlertService;
    }

    @PostMapping
    public ResponseEntity<?> triggerPanic(@RequestBody PanicAlertRequestDTO request) {
        try {
            PanicAlert alert = panicAlertService.triggerPanic(
                    request.getRideId(),
                    request.getUserId(),
                    request.getCurrentLocation()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PanicAlertResponseDTO>> getAllAlerts() {
        List<PanicAlertResponseDTO> alerts = panicAlertService.getAllAlerts()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/unresolved")
    public ResponseEntity<List<PanicAlertResponseDTO>> getUnresolvedAlerts() {
        List<PanicAlertResponseDTO> alerts = panicAlertService.getAllUnresolvedAlerts()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<?> getAlertById(@PathVariable Long alertId) {
        try {
            PanicAlert alert = panicAlertService.getAlertById(alertId);
            return ResponseEntity.ok(mapToDTO(alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(
            @PathVariable Long alertId,
            @RequestBody ResolvePanicRequestDTO request) {
        try {
            PanicAlert alert = panicAlertService.resolveAlert(
                    alertId,
                    request.getAdminId(),
                    request.getNotes()
            );
            return ResponseEntity.ok(mapToDTO(alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private PanicAlertResponseDTO mapToDTO(PanicAlert alert) {
        PanicAlertResponseDTO dto = new PanicAlertResponseDTO();
        dto.setId(alert.getId());
        dto.setRideId(alert.getRide().getId());
        dto.setTriggeredByUserId(alert.getTriggeredBy().getId());
        dto.setTriggeredByName(alert.getTriggeredBy().getFirstName() + " " + alert.getTriggeredBy().getLastName());
        dto.setCurrentLocation(alert.getCurrentLocation());
        dto.setTriggeredAt(alert.getTriggeredAt());
        dto.setResolved(alert.isResolved());
        dto.setResolvedAt(alert.getResolvedAt());
        if (alert.getResolvedBy() != null) {
            dto.setResolvedByAdminId(alert.getResolvedBy().getId());
        }
        dto.setResolutionNotes(alert.getResolutionNotes());
        
        dto.setDriverId(alert.getRide().getDriver().getId());
        dto.setDriverName(alert.getRide().getDriver().getFirstName() + " " + alert.getRide().getDriver().getLastName());
        dto.setStartLocation(alert.getRide().getStartLocation());
        dto.setEndLocation(alert.getRide().getEndLocation());
        
        return dto;
    }
}