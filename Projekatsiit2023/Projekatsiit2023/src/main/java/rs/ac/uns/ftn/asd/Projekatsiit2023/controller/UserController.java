package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.user.UserProfileDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getProfile() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.id = 1L;
        dto.email = "user@mail.com";
        dto.name = "John";
        dto.surname = "Doe";

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestBody UserProfileDTO profile) {

        return ResponseEntity.ok(profile);
    }
}
