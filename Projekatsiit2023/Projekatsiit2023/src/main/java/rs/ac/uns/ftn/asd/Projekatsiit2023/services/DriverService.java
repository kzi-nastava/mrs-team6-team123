package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverRegistrationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverResponseDTO registerDriver(DriverRegistrationRequestDTO request) {
        Driver driver = new Driver();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setAddress(request.getAddress());

        // Temp password
        driver.setPassword("temp123");
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