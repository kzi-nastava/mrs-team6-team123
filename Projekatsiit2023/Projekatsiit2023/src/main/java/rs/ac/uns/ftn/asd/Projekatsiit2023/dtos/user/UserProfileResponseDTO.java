package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;

@Setter
@Getter
public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private UserRole userRole;
    
    // Driver-specific fields (null for passengers)
    private String hoursActive;
    private Integer totalRides;
    private Double rating;
    private VehicleDTO vehicle;

    public UserProfileResponseDTO() {
    }

}
