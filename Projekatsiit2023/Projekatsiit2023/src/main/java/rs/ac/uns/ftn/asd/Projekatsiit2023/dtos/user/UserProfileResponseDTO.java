package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserProfileResponseDTO() {
    }

}
