// PassengerInfoDTO.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PassengerInfoDTO {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
}