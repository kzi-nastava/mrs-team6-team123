package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.ChangePasswordRequest;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserBasicInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 2.3 Profil korisnika - GET
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getProfile(@PathVariable Long userId) {
        UserProfileResponseDTO response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponseDTO> getUserByEmail(@PathVariable String email) {
        UserProfileResponseDTO response = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllActiveUsers(@RequestParam Long excludeUserId) {
        try {
            List<UserBasicInfoDTO> users = userService.getAllActiveUsers(excludeUserId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users: " + e.getMessage());
        }
    }

    // 2.3 Profil korisnika - UPDATE
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(@PathVariable Long userId,
            @RequestBody UserProfileRequestDTO request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // Promena lozinke korisnika
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {
        userService.changeUserPassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // Upload profile photo
    @PostMapping("/{userId}/profile-photo")
    public ResponseEntity<UserProfileResponseDTO> uploadProfilePhoto(@PathVariable Long userId,
            @RequestParam("profileImage") MultipartFile file) {
        UserProfileResponseDTO response = userService.uploadProfilePhoto(userId, file);
        return ResponseEntity.ok(response);
    }

    // Blocking/unblocking users
    @PostMapping("/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok().build();
    }
}
