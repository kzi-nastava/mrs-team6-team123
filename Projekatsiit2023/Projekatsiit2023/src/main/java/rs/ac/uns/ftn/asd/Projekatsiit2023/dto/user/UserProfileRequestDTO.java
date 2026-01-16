package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserProfileRequestDTO() {
    }

}
