package rs.ac.uns.ftn.asd.Projekatsiit2023.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Driver {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String status; // PENDING_ACTIVATION, ACTIVE, INACTIVE, BLOCKED
    private boolean active; // current availability
    private int activeMinutesLast24h; // working time

    // Vehicle
    private String vehicleModel;
    private String vehicleType;
    private String licensePlate;
    private int seats;
    private boolean babyTransport;
    private boolean petTransport;

    public Driver() {
    }

}
