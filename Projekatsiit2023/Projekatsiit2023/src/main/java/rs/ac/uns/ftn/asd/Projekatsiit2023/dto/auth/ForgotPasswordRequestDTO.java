package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequestDTO {
    private String email;

    public ForgotPasswordRequestDTO() {
    }

}