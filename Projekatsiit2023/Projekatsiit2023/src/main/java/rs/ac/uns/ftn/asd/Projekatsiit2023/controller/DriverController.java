package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.DriverResponseDTO;

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
        response.setStatus("PENDING_APPROVAL");

        return ResponseEntity.created(URI.create("/api/drivers/" + response.getId()))
                .body(response);
    }
}
