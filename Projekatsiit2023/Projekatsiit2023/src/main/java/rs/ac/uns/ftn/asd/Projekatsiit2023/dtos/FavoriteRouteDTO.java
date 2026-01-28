package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDTO {
    private Long id;
    private Long routeId;
    private String startLocation;
    private String endLocation;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private LocalDateTime createdAt;
}
