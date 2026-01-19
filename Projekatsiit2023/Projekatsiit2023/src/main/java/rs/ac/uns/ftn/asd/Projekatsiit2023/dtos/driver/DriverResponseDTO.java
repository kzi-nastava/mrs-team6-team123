package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;

@Setter
@Getter
public class DriverResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String vehicleModel;
    private String licensePlate;
    private DriverStatus status; // e.g., ACTIVE, PENDING_APPROVAL

    public DriverResponseDTO() {
    }

}
