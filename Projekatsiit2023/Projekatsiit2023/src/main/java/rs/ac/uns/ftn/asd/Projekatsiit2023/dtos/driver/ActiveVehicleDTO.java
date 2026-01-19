package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActiveVehicleDTO {
    private Long vehicleId;
    private String location;
    private boolean available;

    public ActiveVehicleDTO() {
    }

}
