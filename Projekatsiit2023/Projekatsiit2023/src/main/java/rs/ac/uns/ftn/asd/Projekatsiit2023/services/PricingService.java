package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.pricing.ChangePricingRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.pricing.ShowPricingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Pricing;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PricingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PricingService {

    private final PricingRepository repository;

    public PricingService(PricingRepository repository) {
        this.repository = repository;
    }

    public List<ShowPricingResponseDTO> getPricing() {
        List<Pricing> pricing = repository.findAll();
        List<ShowPricingResponseDTO> pricingResponse = new ArrayList<>();
        for (Pricing p : pricing) {
            pricingResponse.add(mapPricingToShowPricingDTO(p));
        }
        return pricingResponse;
    }

    public void changePrice(ChangePricingRequestDTO dto) {
        String vehicleTypeStr = dto.getVehicleType().toUpperCase();
        VehicleType vehicleType = VehicleType.valueOf(vehicleTypeStr);
        Pricing pricing = repository.findByVehicleType(vehicleType);
        if (pricing != null && pricing.getPrice() == dto.getPrice()) {
            pricing.setPrice(dto.getNewPrice());
            repository.save(pricing);
        }
    }

    public ShowPricingResponseDTO mapPricingToShowPricingDTO(Pricing pricing) {
        ShowPricingResponseDTO dto = new ShowPricingResponseDTO();
        dto.setVehicleType(pricing.getVehicleType().toString());
        dto.setPrice(pricing.getPrice());
        return dto;
    }
}
