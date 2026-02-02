package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String profilePicture;

    public RegistrationRequestDTO() {
    }

}