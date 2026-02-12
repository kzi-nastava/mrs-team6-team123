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
    // Rides Statistics
    private long totalRides;
    private double avgRidesPerDay;
    private List<RideDataPointDTO> ridesData;

    // Kilometers Statistics
    private double totalKmTraveled;
    private double avgKmPerDay;
    private List<RideDataPointDTO> kmData;

    // Amount Spent Statistics
    private double totalAmountSpent;
    private double avgAmountPerDay;
    private List<RideDataPointDTO> amountData;
}
