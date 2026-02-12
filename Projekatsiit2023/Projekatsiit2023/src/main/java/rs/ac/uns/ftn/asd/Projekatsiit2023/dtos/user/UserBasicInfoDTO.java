package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String userRole; // "PASSENGER" or "DRIVER"
}
