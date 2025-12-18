package rs.ac.uns.ftn.asd.Projekatsiit2023.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth.LoginRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth.LoginResponseDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = new LoginResponseDTO();
        response.token = "dummy-jwt-token";

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activate(@PathVariable String token) {
        return ResponseEntity.ok("Account activated");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword() {
        return ResponseEntity.ok("Reset password email sent");
    }
}
