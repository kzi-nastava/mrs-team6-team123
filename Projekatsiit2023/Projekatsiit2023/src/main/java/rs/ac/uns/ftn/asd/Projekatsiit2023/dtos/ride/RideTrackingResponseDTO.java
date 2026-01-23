package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RideTrackingResponseDTO {
    private Long rideId;
    private String driver;
    private String startedAt;
    private String from;
    private String to;
    private String nextStop;
    private double nextStopLatitude;
    private double nextStopLongitude;
    private double currentLatitude;
    private double currentLongitude;
    private double price;
    private int timeLeft;
    private List<String> passengers = new ArrayList<>();
}
