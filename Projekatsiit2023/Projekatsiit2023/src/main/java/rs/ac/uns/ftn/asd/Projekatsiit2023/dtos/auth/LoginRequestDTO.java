package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
    private String email;
    private String password;

    public LoginRequestDTO() {
    }

}