package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.IrregularityReport;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.IrregularityReportService;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.DriverService;

import java.net.URI;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {
    private final IrregularityReportService reportService;
    private final DriverService driverService;

    public DriverController(
            DriverService driverService,
            IrregularityReportService reportService) {
        this.driverService = driverService;
        this.reportService = reportService;
    }

    // 2.2.3 Registracija vozača
    @PostMapping
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody DriverRegistrationRequestDTO request) {
        DriverResponseDTO response = driverService.registerDriver(request);
        return ResponseEntity.created(URI.create("/api/drivers/" + response.getId()))
                .body(response);
    }

    @PostMapping({"/report"})
    public ResponseEntity<?> reportDriver(@RequestBody ReportDriverRequestDTO request) {
        try {
            reportService.reportDriver(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }
}
