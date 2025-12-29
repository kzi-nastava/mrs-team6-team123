package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.UserProfileResponseDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 2.3 Profil korisnika - GET
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getProfile(@PathVariable Long userId) {
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setId(userId);
        response.setFirstName("Test");
        response.setLastName("User");
        response.setEmail("user@example.com");
        response.setPhone("+381601234567");
        return ResponseEntity.ok(response);
    }

    // 2.3 Profil korisnika - UPDATE
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(@PathVariable Long userId,
            @RequestBody UserProfileRequestDTO request) {
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setId(userId);
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        return ResponseEntity.ok(response);
    }

    // Optional: list users with filter param (name)
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDTO>> listUsers(@RequestParam(required = false) String name) {
        List<UserProfileResponseDTO> users = new ArrayList<>();
        UserProfileResponseDTO u = new UserProfileResponseDTO();
        u.setId(1L);
        u.setFirstName("Ana");
        u.setLastName("Ivić");
        u.setEmail("ana@example.com");
        u.setPhone("+381601234567");
        users.add(u);
        return ResponseEntity.ok(users);
    }
}
