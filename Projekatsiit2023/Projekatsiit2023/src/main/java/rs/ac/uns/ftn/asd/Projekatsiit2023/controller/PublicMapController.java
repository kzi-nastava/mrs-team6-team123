package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ActiveVehicleDTO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/public-map")
public class PublicMapController {

    @GetMapping({"/active"})
    public ResponseEntity<List<ActiveVehicleDTO>> getActiveVehicles() {
        List<ActiveVehicleDTO> response = new ArrayList<>();
        response.add(new ActiveVehicleDTO());
        return ResponseEntity.ok(response);
    }
}
