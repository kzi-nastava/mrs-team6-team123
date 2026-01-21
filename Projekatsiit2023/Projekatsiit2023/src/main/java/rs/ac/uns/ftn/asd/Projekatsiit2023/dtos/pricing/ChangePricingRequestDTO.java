package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.pricing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePricingRequestDTO {
    private String vehicleType;
    private double price;
    private double newPrice;
}
