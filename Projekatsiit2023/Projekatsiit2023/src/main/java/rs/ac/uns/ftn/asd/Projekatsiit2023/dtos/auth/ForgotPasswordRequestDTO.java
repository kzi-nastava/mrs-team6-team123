package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequestDTO {
    private String email;

    public ForgotPasswordRequestDTO() {
    }

}