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

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.VehicleDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PendingDriverProfileChangeRepository pendingChangeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_EMAIL = "admin@taxiapp.com"; // TODO: Get from config or admin table
    private final String uploadDir = "uploads/profile-images/";

    public UserService(UserRepository userRepository,
            PendingDriverProfileChangeRepository pendingChangeRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pendingChangeRepository = pendingChangeRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapUserToUserResponseDTO(user);
    }

    public UserProfileResponseDTO updateProfile(Long userId, UserProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            Driver driver = (Driver) user;

            boolean hasChanges = !user.getFirstName().equals(dto.getFirstName()) ||
                    !user.getLastName().equals(dto.getLastName()) ||
                    !user.getEmail().equals(dto.getEmail()) ||
                    !user.getPhone().equals(dto.getPhone()) ||
                    !user.getAddress().equals(dto.getAddress());

            if (!hasChanges) {
                return mapUserToUserResponseDTO(user);
            }

            PendingDriverProfileChange pendingChange = new PendingDriverProfileChange();
            pendingChange.setDriver(driver);
            pendingChange.setFirstName(dto.getFirstName());
            pendingChange.setLastName(dto.getLastName());
            pendingChange.setEmail(dto.getEmail());
            pendingChange.setPhone(dto.getPhone());
            pendingChange.setAddress(dto.getAddress());
            pendingChange.setStatus(ChangeStatus.PENDING);

            PendingDriverProfileChange saved = pendingChangeRepository.save(pendingChange);

            StringBuilder changes = new StringBuilder();
            if (!user.getFirstName().equals(dto.getFirstName())) {
                changes.append(String.format("First Name: %s → %s\n", user.getFirstName(), dto.getFirstName()));
            }
            if (!user.getLastName().equals(dto.getLastName())) {
                changes.append(String.format("Last Name: %s → %s\n", user.getLastName(), dto.getLastName()));
            }
            if (!user.getEmail().equals(dto.getEmail())) {
                changes.append(String.format("Email: %s → %s\n", user.getEmail(), dto.getEmail()));
            }
            if (!user.getPhone().equals(dto.getPhone())) {
                changes.append(String.format("Phone: %s → %s\n", user.getPhone(), dto.getPhone()));
            }
            if (!user.getAddress().equals(dto.getAddress())) {
                changes.append(String.format("Address: %s → %s\n", user.getAddress(), dto.getAddress()));
            }

            String driverName = user.getFirstName() + " " + user.getLastName();
            emailService.sendDriverProfileChangeNotification(
                    ADMIN_EMAIL,
                    driverName,
                    user.getEmail(),
                    saved.getId(),
                    changes.toString());

            return mapUserToUserResponseDTO(user);
        }

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

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserProfileResponseDTO uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            if (!file.isEmpty()) {
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    String oldFileName = user.getProfileImage().substring(user.getProfileImage().lastIndexOf("/") + 1);
                    Path oldFilePath = Paths.get(uploadDir).toAbsolutePath().resolve(oldFileName);
                    Files.deleteIfExists(oldFilePath);
                }

                Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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

        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            Driver driver = (Driver) user;

            int minutes = driver.getActiveMinutesLast24h();
            dto.setHoursActive(String.format("%dh %dm", minutes / 60, minutes % 60));

            dto.setTotalRides(driver.getTotalRides());
            dto.setRating(driver.getRating());

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

}
