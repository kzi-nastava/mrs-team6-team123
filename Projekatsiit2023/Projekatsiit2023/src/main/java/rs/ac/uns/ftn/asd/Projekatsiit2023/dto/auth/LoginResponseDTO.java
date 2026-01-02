package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String email;
    private String role;

    public LoginResponseDTO() {
    }

}