package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideMonitoringResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.RideMonitoringService;

import java.util.List;

@RestController
@RequestMapping("/api/rides/monitoring")
public class RideMonitoringController {
    private final RideMonitoringService rideMonitoringService;

    public RideMonitoringController(RideMonitoringService rideMonitoringService) {
        this.rideMonitoringService = rideMonitoringService;
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveRides() {
        try {
            List<RideMonitoringResponseDTO> response = rideMonitoringService.getAllActiveRides();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }
}
