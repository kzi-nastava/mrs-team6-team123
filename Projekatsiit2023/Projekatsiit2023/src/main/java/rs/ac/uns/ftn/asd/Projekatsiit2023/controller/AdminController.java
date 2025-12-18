package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.admin.CreateDriverDTO;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/drivers")
    public ResponseEntity<String> createDriver(
            @RequestBody CreateDriverDTO dto) {

        return ResponseEntity.ok("Driver created: " + dto.email);
    }

    @PostMapping("/block/{id}")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok("User blocked: " + id);
    }

    @GetMapping("/reports")
    public ResponseEntity<String> reports() {
        return ResponseEntity.ok("Reports generated");
    }
}
