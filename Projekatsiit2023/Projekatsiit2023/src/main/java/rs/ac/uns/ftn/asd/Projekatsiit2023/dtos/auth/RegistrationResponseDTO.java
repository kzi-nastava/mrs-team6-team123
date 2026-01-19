package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationResponseDTO {
    private String message;
    private Long userId;
    private String email;

    public RegistrationResponseDTO() {
    }

}