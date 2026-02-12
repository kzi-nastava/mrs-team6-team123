package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

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
    public ResponseEntity<StatisticsDTO> getStatistics(
            @RequestParam Long userId,
            @RequestParam String userType,
            @RequestParam(required = false) Long filteredUserId,
            @RequestParam(required = false) String filteredUserType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            startDate = LocalDate.parse(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            endDate = LocalDate.parse(toDate);
        }

        StatisticsDTO statistics;

        if ("ADMIN".equalsIgnoreCase(userType)) {
            if (filteredUserId != null && filteredUserType != null) {
                statistics = reportsService.getUserRideStatisticsByIdAndType(
                        filteredUserId, filteredUserType, startDate, endDate);
            } else {
                statistics = reportsService.getAllFinishedRidesStatistics(startDate, endDate);
            }
        } else {
            statistics = reportsService.getUserRideStatistics(userId, userType, startDate, endDate);
        }

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserBasicInfoDTO>> getAllActiveUsers(
            @RequestParam Long excludeUserId) {
        List<UserBasicInfoDTO> users = reportsService.getAllActiveUsers(excludeUserId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/passengers")
    public ResponseEntity<List<UserBasicInfoDTO>> getAllPassengers() {
        List<UserBasicInfoDTO> passengers = reportsService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }
}
