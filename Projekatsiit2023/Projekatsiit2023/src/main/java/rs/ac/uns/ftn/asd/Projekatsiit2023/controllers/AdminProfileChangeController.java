package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin.ApproveChangeRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin.PendingChangeResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.AdminProfileChangeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profile-changes")
public class AdminProfileChangeController {

    private final AdminProfileChangeService adminProfileChangeService;

    public AdminProfileChangeController(AdminProfileChangeService adminProfileChangeService) {
        this.adminProfileChangeService = adminProfileChangeService;
    }

    @GetMapping
    public ResponseEntity<List<PendingChangeResponseDTO>> getPendingChanges() {
        return ResponseEntity.ok(adminProfileChangeService.getPendingChanges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PendingChangeResponseDTO> getChangeRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminProfileChangeService.getChangeRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewChangeRequest(
            @PathVariable Long id,
            @RequestBody ApproveChangeRequestDTO dto,
            @RequestHeader("X-Admin-Id") Long adminId) { // TODO: Get from JWT token instead
        try {
            PendingChangeResponseDTO response = adminProfileChangeService.reviewChangeRequest(id, dto, adminId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
