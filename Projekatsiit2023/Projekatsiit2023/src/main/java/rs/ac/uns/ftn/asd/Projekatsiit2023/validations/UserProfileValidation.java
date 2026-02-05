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

    // Regex patterns for validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

    // Validation constraints
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_ADDRESS_LENGTH = 5;
    private static final int MAX_ADDRESS_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    public UserProfileValidation() {
    }

    public void validateProfileUpdate(UserProfileRequestDTO dto) {
        validateFirstName(dto.getFirstName());
        validateLastName(dto.getLastName());
        validatePhone(dto.getPhone());
        validateAddress(dto.getAddress());
    }

    public void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        String trimmed = firstName.trim();

        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("First name must be at least " + MIN_NAME_LENGTH + " characters long");
        }

        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("First name must not exceed " + MAX_NAME_LENGTH + " characters");
        }

        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("First name can only contain letters, spaces, hyphens, and apostrophes");
        }
    }

    public void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        String trimmed = lastName.trim();

        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("Last name must be at least " + MIN_NAME_LENGTH + " characters long");
        }

        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Last name must not exceed " + MAX_NAME_LENGTH + " characters");
        }

        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Last name can only contain letters, spaces, hyphens, and apostrophes");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String trimmed = phone.trim();

        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "Invalid phone number format. Must be 7-15 digits, optionally starting with +");
        }
    }

    public void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        String trimmed = address.trim();

        if (trimmed.length() < MIN_ADDRESS_LENGTH) {
            throw new IllegalArgumentException("Address must be at least " + MIN_ADDRESS_LENGTH + " characters long");
        }

        if (trimmed.length() > MAX_ADDRESS_LENGTH) {
            throw new IllegalArgumentException("Address must not exceed " + MAX_ADDRESS_LENGTH + " characters");
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