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

    @GetMapping("/{driverId}/status")
    public ResponseEntity<DriverStatusResponseDTO> getDriverStatus(@PathVariable Long driverId) {
        try {
            DriverStatusResponseDTO response = driverStatusService.getDriverStatus(driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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