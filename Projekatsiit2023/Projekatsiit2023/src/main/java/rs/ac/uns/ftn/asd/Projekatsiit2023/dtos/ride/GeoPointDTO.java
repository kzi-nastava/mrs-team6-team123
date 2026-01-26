package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeoPointDTO {
    private double latitude;
    private double longitude;
    private String location;
}
