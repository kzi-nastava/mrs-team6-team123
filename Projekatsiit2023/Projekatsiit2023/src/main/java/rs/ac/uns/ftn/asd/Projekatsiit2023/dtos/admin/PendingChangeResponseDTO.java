package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin;

import lombok.Data;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.ChangeStatus;

@Data
public class PendingChangeResponseDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private String firstNameOld;
    private String firstNameNew;
    private String lastNameOld;
    private String lastNameNew;
    private String phoneOld;
    private String phoneNew;
    private String addressOld;
    private String addressNew;
    private ChangeStatus status;
}
