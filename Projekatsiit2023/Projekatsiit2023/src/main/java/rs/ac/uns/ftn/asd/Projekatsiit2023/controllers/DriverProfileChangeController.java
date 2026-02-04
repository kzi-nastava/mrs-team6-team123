package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin.PendingChangeResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.DriverProfileChangeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profile-changes")
public class DriverProfileChangeController {

    private final DriverProfileChangeService driverProfileChangeService;

    public DriverProfileChangeController(DriverProfileChangeService driverProfileChangeService) {
        this.driverProfileChangeService = driverProfileChangeService;
    }

    @GetMapping
    public ResponseEntity<List<PendingChangeResponseDTO>> getPendingChanges() {
        return ResponseEntity.ok(driverProfileChangeService.getPendingChanges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PendingChangeResponseDTO> getChangeRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(driverProfileChangeService.getChangeRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveChange(@PathVariable Long id) {
        try {
            PendingChangeResponseDTO response = driverProfileChangeService.approveChange(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineChange(@PathVariable Long id) {
        try {
            PendingChangeResponseDTO response = driverProfileChangeService.declineChange(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
