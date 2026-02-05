package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;

@Component
public class DriverRegistrationValidation {

    private final UserProfileValidation userProfileValidation;
    private final VehicleValidation vehicleValidation;

    public DriverRegistrationValidation(UserProfileValidation userProfileValidation,
            VehicleValidation vehicleValidation) {
        this.userProfileValidation = userProfileValidation;
        this.vehicleValidation = vehicleValidation;
    }

    /**
     * Validates all fields required for driver registration by admin
     */
    public void validateDriverRegistration(DriverRegistrationRequestDTO dto) {
        // Validate personal information
        userProfileValidation.validateFirstName(dto.getFirstName());
        userProfileValidation.validateLastName(dto.getLastName());
        userProfileValidation.validateEmailUniqueness(dto.getEmail());
        userProfileValidation.validatePhone(dto.getPhone());
        userProfileValidation.validateAddress(dto.getAddress());

        // Validate vehicle information
        vehicleValidation.validateVehicle(
                dto.getVehicleModel(),
                dto.getVehicleType(),
                dto.getLicensePlate(),
                dto.getSeats());
    }
}
