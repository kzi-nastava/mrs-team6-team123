package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.util.regex.Pattern;

/**
 * Validation component for user profile data.
 * Contains all validation logic for user profile creation and updates.
 */
@Component
public class UserProfileValidation {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private final UserRepository userRepository;

    public UserProfileValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailUniqueness(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
    }
}