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
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PasswordResetToken;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ActivationTokenRepository;
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
private final DriverStatusService driverStatusService;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider,
                      EmailService emailService,
                      ActivationTokenRepository activationTokenRepository,
                      PasswordResetTokenRepository passwordResetTokenRepository,
                    DriverRepository driverRepository,
                  DriverStatusService driverStatusService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.activationTokenRepository = activationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
            this.driverRepository = driverRepository;
    this.driverStatusService = driverStatusService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
    // Pronađi korisnika po email-u
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

    // Proveri lozinku
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid email or password");
    }

    // Proveri da li je nalog aktiviran
    if (!user.isAccountActivated()) {
        throw new RuntimeException("Account not activated. Please check your email.");
    }

    // Proveri da li je nalog blokiran
    if (user.isAccountBlocked()) {
        throw new RuntimeException("Account is blocked. Contact support.");
    }

    // AKO JE VOZAČ - automatski postaje aktivan pri loginu
    if (user.getUserRole() == UserRole.DRIVER) {
        driverStatusService.activateDriverOnLogin(user.getId());
    }

    // Generiši JWT token
    String token = jwtTokenProvider.generateToken(user);

    // Kreiraj response
    LoginResponseDTO response = new LoginResponseDTO();
    response.setToken(token);
    response.setUserId(user.getId());
    response.setEmail(user.getEmail());
    response.setRole(user.getUserRole());

    return response;
}

    @Transactional
public RegistrationResponseDTO register(RegistrationRequestDTO request) {
    // Validacija
    if (!request.getPassword().equals(request.getConfirmPassword())) {
        throw new RuntimeException("Passwords do not match");
    }

    // Proveri da li već postoji korisnik sa tim email-om
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email already exists");
    }

    // Kreiraj novog korisnika (Passenger)
    Passenger passenger = new Passenger();
    passenger.setEmail(request.getEmail());
    passenger.setPassword(passwordEncoder.encode(request.getPassword()));
    passenger.setFirstName(request.getFirstName());
    passenger.setLastName(request.getLastName());
    passenger.setAddress(request.getAddress());
    passenger.setPhone(request.getPhoneNumber());
    
    // DEFAULT SLIKA ako korisnik nije uneo
    String profilePicture = request.getProfilePicture();
    if (profilePicture == null || profilePicture.trim().isEmpty()) {
        profilePicture = "https://cdn-icons-png.flaticon.com/512/149/149071.png"; // Default avatar
    }
    passenger.setProfileImage(profilePicture);
    
    passenger.setUserRole(UserRole.PASSENGER);
    passenger.setAccountActivated(false);
    passenger.setAccountBlocked(false);
    passenger.setStartedRide(false);

    // Sačuvaj u bazu
    User savedUser = userRepository.save(passenger);
    userRepository.flush();

    // Generiši activation token
    String tokenString = UUID.randomUUID().toString();
    ActivationToken activationToken = new ActivationToken(tokenString, savedUser);
    activationTokenRepository.save(activationToken);

    // Pošalji email
    String activationLink = "http://localhost:4200/activate?token=" + tokenString;
    emailService.sendActivationEmail(savedUser.getEmail(), activationLink);

    // Response
    RegistrationResponseDTO response = new RegistrationResponseDTO();
    response.setMessage("Registration successful. Please check your email to activate your account.");
    response.setUserId(savedUser.getId());
    response.setEmail(savedUser.getEmail());

    return response;
}

public LogoutResponseDTO logout(LogoutRequestDTO request) {
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    LogoutResponseDTO response = new LogoutResponseDTO();

    // Ako je vozač, proveri da li ima aktivnu vožnju
    if (user.getUserRole() == UserRole.DRIVER) {
        if (!driverStatusService.canLogout(user.getId())) {
            response.setSuccess(false);
            response.setMessage("Cannot logout while having an active ride. Please finish or cancel the ride first.");
            return response;
        }

        // Postavi vozača na neaktivan
        Driver driver = driverRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setActive(false);
        // Resetuj pending deactivation flag ako postoji
        if (driver.getActiveMinutesLast24h() < 0) {
            driver.setActiveMinutesLast24h(0);
        }
        driverRepository.save(driver);
    }


    response.setSuccess(true);
    response.setMessage("Successfully logged out.");
    return response;
}

    @Transactional
    public String activateAccount(String token) {
        // Pronađi token
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        // Proveri da li je već aktiviran
        if (activationToken.isActivated()) {
            throw new RuntimeException("Account already activated");
        }

        // Proveri da li je istekao
        if (activationToken.isExpired()) {
            throw new RuntimeException("Activation token expired");
        }

        // Aktiviraj korisnika
        User user = activationToken.getUser();
        user.setAccountActivated(true);
        userRepository.save(user);

        // Označi token kao iskorišćen
        activationToken.setActivatedAt(LocalDateTime.now());
        activationTokenRepository.save(activationToken);

        return "Account successfully activated. You can now log in.";
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequestDTO request) {
        // Pronađi korisnika
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with this email does not exist"));

        // Generiši reset token
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenString, user);
        passwordResetTokenRepository.save(resetToken);

        // Pošalji email
        String resetLink = "http://localhost:4200/reset-password?token=" + tokenString;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return "Password reset link has been sent to your email.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequestDTO request) {
        // Validacija lozinki
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Pronađi token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        // Proveri da li je već iskorišćen
        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token already used");
        }

        // Proveri da li je istekao
        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token expired");
        }

        // Resetuj lozinku
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Označi token kao iskorišćen
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        return "Password has been successfully reset.";
    }
}