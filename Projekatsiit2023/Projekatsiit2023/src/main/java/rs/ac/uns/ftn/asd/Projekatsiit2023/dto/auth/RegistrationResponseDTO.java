package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.auth;

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