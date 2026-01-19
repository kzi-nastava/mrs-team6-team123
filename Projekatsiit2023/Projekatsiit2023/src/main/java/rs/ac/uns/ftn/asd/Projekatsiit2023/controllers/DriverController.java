package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;

import java.net.URI;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    // 2.2.3 Registracija vozača
    @PostMapping
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody DriverRegistrationRequestDTO request) {
        DriverResponseDTO response = new DriverResponseDTO();
        response.setId(100L);
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setVehicleModel(request.getVehicleModel());
        response.setLicensePlate(request.getLicensePlate());
        response.setStatus(DriverStatus.PENDING_APPROVAL);

        return ResponseEntity.created(URI.create("/api/drivers/" + response.getId()))
                .body(response);
    }

    @PostMapping({"/{driverId}/report"})
    public ResponseEntity<ReportDriverResponseDTO> reportDriver(@RequestBody ReportDriverRequestDTO request) {
        ReportDriverResponseDTO response = new ReportDriverResponseDTO();
        response.setRideId(request.getRideId());
        response.setDriverId(request.getDriverId());
        // vehicle ID is loaded based on driver ID
        response.setVehicleId(400L);
        response.setComment(request.getComment());
        return  ResponseEntity.ok(response);
    }
}
