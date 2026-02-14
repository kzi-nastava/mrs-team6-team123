package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import com.sun.jdi.PrimitiveType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ActiveVehicleDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.PublicMapService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public-map")
public class PublicMapController {

    private final PublicMapService service;

    public PublicMapController(PublicMapService service) {
        this.service = service;
    }

    @GetMapping({ "/active" })
    public ResponseEntity<?> getActiveVehicles() {
        try {
            return ResponseEntity.ok(service.getVehicles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching active vehicles." + e.getMessage());
        }
    }

    @GetMapping({ "/active/{driverId}" })
    public ResponseEntity<?> getDriversVehicle(@PathVariable Long driverId) {
        try {
            return ResponseEntity.ok(service.getDriversVehicle(driverId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching driver's active vehicle.");
        }
    }
}
