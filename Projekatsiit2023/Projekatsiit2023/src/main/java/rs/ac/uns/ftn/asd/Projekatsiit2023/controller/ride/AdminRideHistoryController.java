package rs.ac.uns.ftn.asd.Projekatsiit2023.controller.ride;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.AdminRideHistoryDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/ride-history")
public class AdminRideHistoryController {

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<AdminRideHistoryDTO>> getDriverRideHistory(
            @PathVariable Long driverId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String sortBy) {
        List<AdminRideHistoryDTO> response = new ArrayList<>();
        AdminRideHistoryDTO dummyRide = new AdminRideHistoryDTO();
        dummyRide.setRideId(1L);
        dummyRide.setDriverId(driverId);
        response.add(dummyRide);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<AdminRideHistoryDTO>> getPassengerRideHistory(
            @PathVariable Long passengerId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String sortBy) {
        List<AdminRideHistoryDTO> response = new ArrayList<>();
        AdminRideHistoryDTO dummyRide = new AdminRideHistoryDTO();
        dummyRide.setRideId(1L);
        dummyRide.setCreatorId(passengerId);
        response.add(dummyRide);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rideId}/details")
    public ResponseEntity<AdminRideHistoryDTO> getRideDetails(@PathVariable Long rideId) {
        AdminRideHistoryDTO response = new AdminRideHistoryDTO();
        response.setRideId(rideId);
        response.setStartLocation("45.2671 N, 19.8335 E");
        response.setEndLocation("45.2550 N, 19.8450 E");
        response.setPrice(1500.0);
        response.setPanicTriggered(false);
        return ResponseEntity.ok(response);
    }
}