// RideHistoryFilterDTO.java

package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RideHistoryFilterDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String sortBy;      // "date", "price", "startLocation", "endLocation"
    private String sortOrder;   // "asc", "desc"
}