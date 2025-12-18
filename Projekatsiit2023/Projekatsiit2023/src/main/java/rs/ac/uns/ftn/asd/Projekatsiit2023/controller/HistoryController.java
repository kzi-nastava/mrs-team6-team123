package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride.RideDTO;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @GetMapping
    public ResponseEntity<List<RideDTO>> getHistory() {

        List<RideDTO> history = new ArrayList<>();

        RideDTO ride = new RideDTO();
        ride.id = 1L;
        ride.startLocation = "Start";
        ride.endLocation = "End";
        ride.status = "FINISHED";

        history.add(ride);

        return ResponseEntity.ok(history);
    }
}
