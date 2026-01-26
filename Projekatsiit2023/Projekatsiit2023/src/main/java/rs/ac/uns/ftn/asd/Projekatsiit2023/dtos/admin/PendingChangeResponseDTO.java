package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin;

import lombok.Data;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.ChangeStatus;

import java.time.LocalDateTime;

@Data
public class PendingChangeResponseDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private ChangeStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
}
