package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LogoutRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.LogoutResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.RegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.RegistrationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.ForgotPasswordRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth.ResetPasswordRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActivationToken;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.ActiveVehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PasswordResetToken;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActivationTokenRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActiveVehicleRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PasswordResetTokenRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DriverRepository driverRepository;
    private final ActiveVehicleRepository activeVehicleRepository;
    private final DriverStatusService driverStatusService;
    private final PublicMapService publicMapService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            EmailService emailService,
            ActivationTokenRepository activationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            DriverRepository driverRepository, ActiveVehicleRepository activeVehicleRepository,
            DriverStatusService driverStatusService, PublicMapService publicMapService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.activationTokenRepository = activationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.driverRepository = driverRepository;
        this.activeVehicleRepository = activeVehicleRepository;
        this.driverStatusService = driverStatusService;
        this.publicMapService = publicMapService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isAccountActivated()) {
            throw new RuntimeException("Account not activated. Please check your email.");
        }

        if (user.isAccountBlocked()) {
            throw new RuntimeException("Account is blocked. Contact support.");
        }

        if (user.getUserRole() == UserRole.DRIVER) {
            Driver driver = driverRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driverStatusService.activateDriverOnLogin(user.getId());
            Optional<ActiveVehicle> existing =
                    activeVehicleRepository.findByVehicleId(driver.getVehicle().getId());

            ActiveVehicle av;
            if (existing.isEmpty()) {
                av = new ActiveVehicle();
                av.setVehicle(driver.getVehicle());
            } else {
                av = existing.get();
            }
            av.setCurrentLatitude(45.2576);
            av.setCurrentLongitude(19.8442);
            av.setTargetLatitude(0.0);
            av.setTargetLongitude(0.0);
            av.setAvailable(true);
            av.setRouteCoordinates(null);
            activeVehicleRepository.save(av);
            publicMapService.getDriversVehicle(user.getId());
        }

        String token = jwtTokenProvider.generateToken(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getUserRole());

        return response;
    }

    @Transactional
    public RegistrationResponseDTO register(RegistrationRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Passenger passenger = new Passenger();
        passenger.setEmail(request.getEmail());
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));
        passenger.setFirstName(request.getFirstName());
        passenger.setLastName(request.getLastName());
        passenger.setAddress(request.getAddress());
        passenger.setPhone(request.getPhoneNumber());

        String profilePicture = request.getProfilePicture();
        if (profilePicture == null || profilePicture.trim().isEmpty()) {
            profilePicture = "https://cdn-icons-png.flaticon.com/512/149/149071.png"; // Default avatar
        }
        passenger.setProfileImage(profilePicture);

        passenger.setUserRole(UserRole.PASSENGER);
        passenger.setAccountActivated(false);
        passenger.setAccountBlocked(false);
        passenger.setStartedRide(false);

        User savedUser = userRepository.save(passenger);
        userRepository.flush();

        String tokenString = UUID.randomUUID().toString();
        ActivationToken activationToken = new ActivationToken(tokenString, savedUser);
        activationTokenRepository.save(activationToken);

        String activationLink = "http://localhost:4200/activate?token=" + tokenString;
        emailService.sendActivationEmail(savedUser.getEmail(), activationLink);

        RegistrationResponseDTO response = new RegistrationResponseDTO();
        response.setMessage("Registration successful. Please check your email to activate your account.");
        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());

        return response;
    }

    @Transactional
    public LogoutResponseDTO logout(LogoutRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LogoutResponseDTO response = new LogoutResponseDTO();

        if (user.getUserRole() == UserRole.DRIVER) {
            if (!driverStatusService.canLogout(user.getId())) {
                response.setSuccess(false);
                response.setMessage(
                        "Cannot logout while having an active ride. Please finish or cancel the ride first.");
                return response;
            }

            Driver driver = driverRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (driver.getActiveMinutesLast24h() < 0) {
                driver.setActiveMinutesLast24h(0);
            }
            driverRepository.save(driver);

            ActiveVehicle av = activeVehicleRepository.findByVehicleId(driver.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Active vehicle not found for driver"));
            av.setCurrentRide(null);
            av.setRouteIndex(0);
            av.setRouteCoordinates(null);
            av.setAvailable(false);
            av.setTargetLatitude(0.0);
            av.setTargetLongitude(0.0);
            activeVehicleRepository.save(av);
        }

        response.setSuccess(true);
        response.setMessage("Successfully logged out.");
        return response;
    }

    @Transactional
    public String activateAccount(String token) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (activationToken.isActivated()) {
            throw new RuntimeException("Account already activated");
        }

        if (activationToken.isExpired()) {
            throw new RuntimeException("Activation token expired");
        }

        User user = activationToken.getUser();
        user.setAccountActivated(true);
        userRepository.save(user);

        activationToken.setActivatedAt(LocalDateTime.now());
        activationTokenRepository.save(activationToken);

        return "Account successfully activated. You can now log in.";
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with this email does not exist"));

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenString, user);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + tokenString;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return "Password reset link has been sent to your email.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token already used");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setAccountActivated(true);

        // If user is a driver, set active and add vehicle to active_vehicles
        if (user instanceof Driver driver) {
            driver.setActive(true);
            // Add vehicle to active_vehicles if not already present
            if (driver.getVehicle() != null) {
                if (activeVehicleRepository.findByVehicleId(driver.getVehicle().getId()).isEmpty()) {
                    ActiveVehicle av = new ActiveVehicle();
                    av.setVehicle(driver.getVehicle());
                    av.setCurrentLatitude(0.0); // Default, update as needed
                    av.setCurrentLongitude(0.0);
                    av.setAvailable(true);
                    activeVehicleRepository.save(av);
                }
            }
        }
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        return "Password has been successfully reset.";
    }
}