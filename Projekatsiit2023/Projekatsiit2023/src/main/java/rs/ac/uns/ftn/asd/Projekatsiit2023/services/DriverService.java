package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PasswordResetToken;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PasswordResetTokenRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.DriverRegistrationValidation;

import java.util.UUID;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DriverRegistrationValidation driverRegistrationValidation;

    public DriverService(DriverRepository driverRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            DriverRegistrationValidation driverRegistrationValidation) {
        this.driverRepository = driverRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.driverRegistrationValidation = driverRegistrationValidation;
    }

    @Transactional
    public DriverResponseDTO registerDriver(DriverRegistrationRequestDTO request) {
        // Validate all input data
        driverRegistrationValidation.validateDriverRegistration(request);

        Driver driver = new Driver();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setAddress(request.getAddress());

        // Temporary password - will be replaced when driver sets their own via password
        // reset link
        driver.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        driver.setUserRole(UserRole.DRIVER);
        driver.setAccountActivated(false);
        driver.setAccountBlocked(false);

        // Driver defaults
        driver.setStatus(DriverStatus.PENDING_APPROVAL);
        driver.setActive(false);
        driver.setActiveMinutesLast24h(0);
        driver.setTotalRides(0);
        driver.setRating(0.0);

        // Vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel(request.getVehicleModel());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setSeats(request.getSeats());
        vehicle.setBabyTransport(request.isBabyTransport());
        vehicle.setPetTransport(request.isPetTransport());
        driver.setVehicle(vehicle);

        Driver saved = driverRepository.save(driver);

        // Generate password reset token for driver to set their own password
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenString, saved);
        passwordResetTokenRepository.save(resetToken);

        // Send email with password setup link
        String setupLink = "http://localhost:4200/reset-password?token=" + tokenString;
        emailService.sendDriverWelcomeEmail(saved.getEmail(), saved.getFirstName(), setupLink);

        return mapToResponse(saved);
    }

    private DriverResponseDTO mapToResponse(Driver driver) {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setId(driver.getId());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setEmail(driver.getEmail());
        dto.setPhone(driver.getPhone());
        if (driver.getVehicle() != null) {
            dto.setVehicleModel(driver.getVehicle().getVehicleModel());
            dto.setLicensePlate(driver.getVehicle().getLicensePlate());
        }
        dto.setStatus(driver.getStatus());
        return dto;
    }
}