package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.ForgotPasswordRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.RegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.RegistrationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.ResetPasswordRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken("dummy-jwt-token-12345");
        response.setUserId(1L);
        response.setEmail(loginRequest.getEmail());
        response.setRole(UserRole.PASSENGER);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody RegistrationRequestDTO registrationRequest) {
        RegistrationResponseDTO response = new RegistrationResponseDTO();
        response.setMessage("Registration successful. Please check your email to activate your account.");
        response.setUserId(100L);
        response.setEmail(registrationRequest.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        return ResponseEntity.ok("Account successfully activated. You can now log in.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}