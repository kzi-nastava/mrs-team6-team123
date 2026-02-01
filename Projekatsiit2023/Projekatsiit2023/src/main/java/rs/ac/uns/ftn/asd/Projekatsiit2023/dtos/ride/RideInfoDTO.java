package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RideInfoDTO {
    private String driver;
    private String startedAt;
    private String from;
    private String to;
    private double price;
    private int duration;
    private List<String> passengers = new ArrayList<>();
    private List<String> reports = new ArrayList<>();
    private RideStatus status;
}
