package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VehicleDTO {
    private String model;
    private String type;
    private String licensePlate;
    private int capacity;
    private boolean babiesAllowed;
    private boolean petsAllowed;

    public VehicleDTO() {
    }
}
