package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;

@Setter
@Getter
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String email;
    private UserRole role;

    public LoginResponseDTO() {
    }

}