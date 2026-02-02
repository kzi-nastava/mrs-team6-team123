package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.GraphHopperService;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final GraphHopperService graphHopperService;

    public RouteController(GraphHopperService graphHopperService) {
        this.graphHopperService = graphHopperService;
    }

    @PostMapping
    public ResponseEntity<?> getRoute(@RequestBody List<GeoPointDTO> points) {
        try {

            return ResponseEntity.ok(graphHopperService.getRoute(points).getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }
}

