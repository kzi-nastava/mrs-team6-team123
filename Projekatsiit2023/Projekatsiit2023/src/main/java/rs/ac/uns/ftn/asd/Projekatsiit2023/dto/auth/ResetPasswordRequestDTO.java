package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequestDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequestDTO() {
    }

}