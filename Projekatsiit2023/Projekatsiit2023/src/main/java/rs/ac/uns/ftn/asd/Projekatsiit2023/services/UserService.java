package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.ChangeStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PendingDriverProfileChange;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PendingDriverProfileChangeRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserBasicInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.VehicleDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.UserProfileValidation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PendingDriverProfileChangeRepository pendingChangeRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileValidation userProfileValidation;
    private final String uploadDir = "./uploads/profile-images/";

    public UserService(UserRepository userRepository,
            PendingDriverProfileChangeRepository pendingChangeRepository,
            NotificationService notificationService,
            PasswordEncoder passwordEncoder,
            UserProfileValidation userProfileValidation) {
        this.userRepository = userRepository;
        this.pendingChangeRepository = pendingChangeRepository;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.userProfileValidation = userProfileValidation;
    }

    /**
     * Get all active users (both passengers and drivers), excluding one user
     */
    public List<UserBasicInfoDTO> getAllActiveUsers(Long excludeUserId) {
        List<UserBasicInfoDTO> allUsers = new ArrayList<>();

        // Add all active passengers except the excluded one
        allUsers.addAll(userRepository.findAll().stream()
                .filter(p -> p.isAccountActivated() && !p.getId().equals(excludeUserId))
                .map(p -> {
                    UserBasicInfoDTO dto = new UserBasicInfoDTO();
                    dto.setId(p.getId());
                    dto.setEmail(p.getEmail());
                    dto.setFirstName(p.getFirstName());
                    dto.setLastName(p.getLastName());
                    dto.setUserRole(UserRole.PASSENGER);
                    dto.setAccountBlocked(p.isAccountBlocked());
                    return dto;
                })
                .collect(Collectors.toList()));

        return allUsers;
    }

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapUserToUserResponseDTO(user);
    }

    public UserProfileResponseDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapUserToUserResponseDTO(user);
    }

    public UserProfileResponseDTO updateProfile(Long userId, UserProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If user is a driver, create pending change request instead of direct update
        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            Driver driver = (Driver) user;

            // Check if there are actual changes
            boolean hasChanges = !user.getFirstName().equals(dto.getFirstName()) ||
                    !user.getLastName().equals(dto.getLastName()) ||
                    !user.getPhone().equals(dto.getPhone()) ||
                    !user.getAddress().equals(dto.getAddress());

            if (!hasChanges) {
                return mapUserToUserResponseDTO(user);
            }

            // Create pending change request
            PendingDriverProfileChange pendingChange = new PendingDriverProfileChange();
            pendingChange.setDriver(driver);
            pendingChange.setFirstName(dto.getFirstName());
            pendingChange.setLastName(dto.getLastName());
            pendingChange.setPhone(dto.getPhone());
            pendingChange.setAddress(dto.getAddress());
            pendingChange.setStatus(ChangeStatus.PENDING);

            PendingDriverProfileChange saved = pendingChangeRepository.save(pendingChange);

            // Build changes description for email
            StringBuilder changes = new StringBuilder();
            if (!user.getFirstName().equals(dto.getFirstName())) {
                changes.append(String.format("First Name: %s → %s\n", user.getFirstName(), dto.getFirstName()));
            }
            if (!user.getLastName().equals(dto.getLastName())) {
                changes.append(String.format("Last Name: %s → %s\n", user.getLastName(), dto.getLastName()));
            }
            if (!user.getPhone().equals(dto.getPhone())) {
                changes.append(String.format("Phone: %s → %s\n", user.getPhone(), dto.getPhone()));
            }
            if (!user.getAddress().equals(dto.getAddress())) {
                changes.append(String.format("Address: %s → %s\n", user.getAddress(), dto.getAddress()));
            }

            // Send notification to admin(s)
            String driverName = user.getFirstName() + " " + user.getLastName();
            List<User> admins = userRepository.findByUserRole(UserRole.ADMIN);

            for (User admin : admins) {
                String notificationLink = String.valueOf(saved.getId());
                notificationService.sendNotification(
                        admin.getId(),
                        "Driver Profile Change Request",
                        driverName + " requested a profile edit. Click to review.",
                        notificationLink);
            }

            // Return current profile (unchanged)
            return mapUserToUserResponseDTO(user);
        }

        // For non-drivers (passengers, admins), apply changes directly
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        userRepository.save(user);
        return mapUserToUserResponseDTO(user);
    }

    public void changeUserPassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordMatches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // Validate new password
        userProfileValidation.validatePassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserProfileResponseDTO uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            if (!file.isEmpty()) {

                // Delete old image
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    String oldFileName = user.getProfileImage().substring(user.getProfileImage().lastIndexOf("/") + 1);
                    Path oldFilePath = Paths.get(uploadDir).toAbsolutePath().resolve(oldFileName);
                    Files.deleteIfExists(oldFilePath);
                }

                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save with unique filename
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save URL to DB
                String imageUrl = "http://localhost:8080/uploads/profile-images/" + fileName;
                user.setProfileImage(imageUrl);
                userRepository.save(user);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload profile photo: " + e.getMessage());
        }

        return mapUserToUserResponseDTO(user);
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    private UserProfileResponseDTO mapUserToUserResponseDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setUserRole(user.getUserRole());
        dto.setProfileImage(user.getProfileImage());

        // If user is a driver, include driver-specific data
        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            Driver driver = (Driver) user;

            // Convert active minutes to hours format
            int minutes = driver.getActiveMinutesLast24h();
            dto.setHoursActive(String.format("%dh %dm", minutes / 60, minutes % 60));

            // Get actual ride count and rating from database
            dto.setTotalRides(driver.getTotalRides());
            dto.setRating(driver.getRating());

            // Map vehicle if exists
            if (driver.getVehicle() != null) {
                dto.setVehicle(mapVehicleToDTO(driver.getVehicle()));
            }
        }

        return dto;
    }

    private VehicleDTO mapVehicleToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setModel(vehicle.getVehicleModel());
        dto.setType(vehicle.getVehicleType().toString());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCapacity(vehicle.getSeats());
        dto.setBabiesAllowed(vehicle.isBabyTransport());
        dto.setPetsAllowed(vehicle.isPetTransport());
        return dto;
    }

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setAccountBlocked(true);
        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            ((Driver) user).setActive(false);
        }
        userRepository.save(user);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setAccountBlocked(false);
        userRepository.save(user);
    }

}
