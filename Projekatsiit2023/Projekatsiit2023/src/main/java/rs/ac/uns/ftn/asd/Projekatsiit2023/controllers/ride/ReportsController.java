package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StatisticsDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserBasicInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.ReportsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(
            @RequestParam Long userId,
            @RequestParam String userType,
            @RequestParam(required = false) Long filteredUserId,
            @RequestParam(required = false) String filteredUserType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        try {
            StatisticsDTO statistics;

            // Parse dates if provided
            LocalDate startDate = fromDate != null ? LocalDate.parse(fromDate) : null;
            LocalDate endDate = toDate != null ? LocalDate.parse(toDate) : null;

            // If admin with filteredUserId and filteredUserType, get that specific user's
            // rides
            if ("ADMIN".equalsIgnoreCase(userType) && filteredUserId != null && filteredUserType != null) {
                statistics = reportsService.getUserRideStatisticsByIdAndType(filteredUserId, filteredUserType,
                        startDate, endDate);
            }
            // If admin without filter, get all rides
            else if ("ADMIN".equalsIgnoreCase(userType)) {
                statistics = reportsService.getAllFinishedRidesStatistics(startDate, endDate);
            }
            // Otherwise get current user's rides
            else {
                statistics = reportsService.getUserRideStatistics(userId, userType, startDate, endDate);
            }

            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching statistics: " + e.getMessage());
        }
    }

    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        try {
            List<UserBasicInfoDTO> passengers = reportsService.getAllPassengers();
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching passengers: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllActiveUsers(@RequestParam Long excludeUserId) {
        try {
            List<UserBasicInfoDTO> users = reportsService.getAllActiveUsers(excludeUserId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users: " + e.getMessage());
        }
    }

    @GetMapping("/{passengerId}/statistics")
    public ResponseEntity<?> getPassengerStatistics(@PathVariable Long passengerId) {
        try {
            StatisticsDTO statistics = reportsService.getPassengerRideStatistics(passengerId, null, null);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching statistics: " + e.getMessage());
        }
    }
}
