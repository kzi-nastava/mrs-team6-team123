package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideDataPointDTO {
    private LocalDate date;
    private double value;
}
