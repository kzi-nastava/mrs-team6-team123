package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ActiveVehicleDTO {
    private Long vehicleId;
    private double latitude;
    private double longitude;
    private boolean available;

    public ActiveVehicleDTO(
            Long vehicleId,
            double latitude,
            double longitude,
            boolean available) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }
}
