package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole userRole;
    private boolean accountBlocked;
}
