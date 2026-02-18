package rs.ac.uns.ftn.asd.Projekatsiit2023.validations;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.VehicleRepository;

@Component
public class VehicleValidation {

    private final VehicleRepository vehicleRepository;

    public VehicleValidation(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void validateLicensePlateUniqueness(String licensePlate) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
            throw new IllegalArgumentException(
                    "Vehicle with this license plate already exists");
        }
    }
}
