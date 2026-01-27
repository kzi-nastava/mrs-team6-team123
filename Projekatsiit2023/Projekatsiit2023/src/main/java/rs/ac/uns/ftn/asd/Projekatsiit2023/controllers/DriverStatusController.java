// DriverStatusController.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverStatusRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverStatusResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.DriverStatusService;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverStatusController {

    private final DriverStatusService driverStatusService;

    public DriverStatusController(DriverStatusService driverStatusService) {
        this.driverStatusService = driverStatusService;
    }

    /**
     * GET /api/drivers/{driverId}/status
     * Vraća trenutni status vozača
     */
    @GetMapping("/{driverId}/status")
    public ResponseEntity<DriverStatusResponseDTO> getDriverStatus(@PathVariable Long driverId) {
        try {
            DriverStatusResponseDTO response = driverStatusService.getDriverStatus(driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/drivers/{driverId}/status
     * Menja status vozača (aktivan/neaktivan)
     */
    @PutMapping("/{driverId}/status")
    public ResponseEntity<DriverStatusResponseDTO> changeDriverStatus(
            @PathVariable Long driverId,
            @RequestBody DriverStatusRequestDTO request) {
        try {
            DriverStatusResponseDTO response = driverStatusService.changeDriverStatus(driverId, request.isActive());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/drivers/{driverId}/can-logout
     * Proverava da li vozač može da se odjavi
     */
    @GetMapping("/{driverId}/can-logout")
    public ResponseEntity<Boolean> canLogout(@PathVariable Long driverId) {
        try {
            boolean canLogout = driverStatusService.canLogout(driverId);
            return ResponseEntity.ok(canLogout);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}