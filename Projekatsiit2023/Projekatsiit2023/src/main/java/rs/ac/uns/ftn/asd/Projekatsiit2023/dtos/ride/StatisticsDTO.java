package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long totalRides;
    private Double avgRidesPerDay;
    private List<RideDataPointDTO> ridesData;
    
    private Double totalKmTraveled;
    private Double avgKmPerDay;
    private List<RideDataPointDTO> kmData;
    
    private Double totalAmountSpent;
    private Double avgAmountPerDay;
    private List<RideDataPointDTO> amountData;
}
