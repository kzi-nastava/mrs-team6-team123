package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

@Setter
@Getter
public class DriverRegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String vehicleModel;
    private VehicleType vehicleType; // STANDARD | LUX | VAN
    private String licensePlate;
    private int seats;
    private boolean babyTransport; // allows babies
    private boolean petTransport; // allows pets

    public DriverRegistrationRequestDTO() {
    }

}
