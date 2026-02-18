package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class DriverRegistrationRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "Driver's first name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "Driver's last name")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "Driver's email address")
    private String email;

    @NotBlank(message = "Address is required")
    @Schema(description = "Driver's address")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number format is invalid")
    @Schema(description = "Driver's phone number")
    private String phone;

    @NotBlank(message = "Vehicle model is required")
    @Schema(description = "Driver's vehicle model")
    private String vehicleModel;

    @NotNull(message = "Vehicle type is required")
    @Schema(description = "Type of vehicle: STANDARD, LUX, VAN")
    private VehicleType vehicleType;

    @NotBlank(message = "License plate is required")
    @Schema(description = "Vehicle license plate")
    private String licensePlate;

    @Min(value = 1, message = "Vehicle must have at least 1 seat")
    @Max(value = 8, message = "Vehicle can have at most 8 seats")
    @Schema(description = "Number of seats in the vehicle")
    private int seats;

    @Schema(description = "Whether vehicle allows baby transport")
    private boolean babyTransport;

    @Schema(description = "Whether vehicle allows pet transport")
    private boolean petTransport;
}
