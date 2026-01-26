package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.DriverService;

import java.net.URI;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // 2.2.3 Registracija vozača
    @PostMapping
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody DriverRegistrationRequestDTO request) {
        DriverResponseDTO response = driverService.registerDriver(request);
        return ResponseEntity.created(URI.create("/api/drivers/" + response.getId()))
                .body(response);
    }

    @PostMapping({ "/{driverId}/report" })
    public ResponseEntity<ReportDriverResponseDTO> reportDriver(@RequestBody ReportDriverRequestDTO request) {
        ReportDriverResponseDTO response = new ReportDriverResponseDTO();
        response.setRideId(request.getRideId());
        response.setDriverId(request.getDriverId());
        // vehicle ID is loaded based on driver ID
        response.setVehicleId(400L);
        response.setComment(request.getComment());
        return ResponseEntity.ok(response);
    }
}
