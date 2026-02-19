package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;

@Component
public class DriverRegistrationValidation {

    private final UserProfileValidation userProfileValidation;
    private final VehicleValidation vehicleValidation;

    public DriverRegistrationValidation(
            UserProfileValidation userProfileValidation,
            VehicleValidation vehicleValidation) {
        this.userProfileValidation = userProfileValidation;
        this.vehicleValidation = vehicleValidation;
    }

    /**
     * Business validation for driver registration.
     */
    public void validateDriverRegistration(DriverRegistrationRequestDTO dto) {

        // Email must be unique in the system
        userProfileValidation.validateEmailUniqueness(dto.getEmail());

        // Vehicle-related business rules
        vehicleValidation.validateLicensePlateUniqueness(dto.getLicensePlate());
    }

}
