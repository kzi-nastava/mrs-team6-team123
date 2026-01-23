package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.VehicleDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToUserResponseDTO(user);
    }

    public UserProfileResponseDTO updateProfile(Long userId, UserProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        userRepository.save(user);
        return mapUserToUserResponseDTO(user);
    }

    private UserProfileResponseDTO mapUserToUserResponseDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setUserRole(user.getUserRole());

        // If user is a driver, include driver-specific data
        if (user.getUserRole() == UserRole.DRIVER && user instanceof Driver) {
            Driver driver = (Driver) user;

            // Convert active minutes to hours format
            int minutes = driver.getActiveMinutesLast24h();
            dto.setHoursActive(String.format("%dh %dm", minutes / 60, minutes % 60));

            // Get actual ride count and rating from database
            dto.setTotalRides(driver.getTotalRides());
            dto.setRating(driver.getRating());

            // Map vehicle if exists
            if (driver.getVehicle() != null) {
                dto.setVehicle(mapVehicleToDTO(driver.getVehicle()));
            }
        }

        return dto;
    }

    private VehicleDTO mapVehicleToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setModel(vehicle.getVehicleModel());
        dto.setType(vehicle.getVehicleType().toString());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCapacity(vehicle.getSeats());
        dto.setBabiesAllowed(vehicle.isBabyTransport());
        dto.setPetsAllowed(vehicle.isPetTransport());
        return dto;
    }

}
