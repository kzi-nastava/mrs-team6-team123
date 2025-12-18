package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panic")
public class PanicController {

    @PostMapping("/{rideId}")
    public ResponseEntity<String> triggerPanic(@PathVariable Long rideId) {
        return ResponseEntity.ok("Panic triggered for ride " + rideId);
    }

    @GetMapping
    public ResponseEntity<String> getPanics() {
        return ResponseEntity.ok("List of panic notifications");
    }
}
