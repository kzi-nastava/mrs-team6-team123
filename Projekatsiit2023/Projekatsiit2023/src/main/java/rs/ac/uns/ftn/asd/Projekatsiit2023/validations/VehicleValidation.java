package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.util.regex.Pattern;

@Component
public class VehicleValidation {

    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
            "^[A-Z0-9\\s-]+$");

    private static final int MIN_VEHICLE_MODEL_LENGTH = 2;
    private static final int MAX_VEHICLE_MODEL_LENGTH = 50;
    private static final int MIN_SEATS = 1;
    private static final int MAX_SEATS = 15;
    private static final int MIN_LICENSE_PLATE_LENGTH = 2;
    private static final int MAX_LICENSE_PLATE_LENGTH = 20;

    public VehicleValidation() {
    }

    public void validateVehicle(String vehicleModel, VehicleType vehicleType,
            String licensePlate, int seats) {
        validateVehicleModel(vehicleModel);
        validateVehicleType(vehicleType);
        validateLicensePlate(licensePlate);
        validateSeats(seats);
    }

    public void validateVehicleModel(String vehicleModel) {
        if (vehicleModel == null || vehicleModel.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle model is required");
        }

        String trimmed = vehicleModel.trim();

        if (trimmed.length() < MIN_VEHICLE_MODEL_LENGTH) {
            throw new IllegalArgumentException(
                    "Vehicle model must be at least " + MIN_VEHICLE_MODEL_LENGTH + " characters long");
        }

        if (trimmed.length() > MAX_VEHICLE_MODEL_LENGTH) {
            throw new IllegalArgumentException(
                    "Vehicle model must not exceed " + MAX_VEHICLE_MODEL_LENGTH + " characters");
        }
    }

    public void validateVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        // Ensure it's one of the valid enum values
        try {
            VehicleType.valueOf(vehicleType.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid vehicle type. Must be one of: STANDARD, LUX, VAN");
        }
    }

    public void validateLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }

        String trimmed = licensePlate.trim();

        if (trimmed.length() < MIN_LICENSE_PLATE_LENGTH) {
            throw new IllegalArgumentException(
                    "License plate must be at least " + MIN_LICENSE_PLATE_LENGTH + " characters long");
        }

        if (trimmed.length() > MAX_LICENSE_PLATE_LENGTH) {
            throw new IllegalArgumentException(
                    "License plate must not exceed " + MAX_LICENSE_PLATE_LENGTH + " characters");
        }

        if (!LICENSE_PLATE_PATTERN.matcher(trimmed.toUpperCase()).matches()) {
            throw new IllegalArgumentException(
                    "Invalid license plate format. Can only contain letters, numbers and spaces");
        }
    }

    public void validateSeats(int seats) {
        if (seats < MIN_SEATS) {
            throw new IllegalArgumentException(
                    "Number of seats must be at least " + MIN_SEATS);
        }

        if (seats > MAX_SEATS) {
            throw new IllegalArgumentException(
                    "Number of seats must not exceed " + MAX_SEATS);
        }
    }
}
