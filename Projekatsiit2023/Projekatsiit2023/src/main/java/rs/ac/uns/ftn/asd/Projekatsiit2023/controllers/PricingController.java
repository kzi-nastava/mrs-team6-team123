package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.pricing.ChangePricingRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.pricing.ShowPricingResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.PricingService;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService service;

    public PricingController(PricingService service) {
        this.service = service;
    }

    @GetMapping("/get-pricing")
    public ResponseEntity<?> getPricing() {
        try {
            List<ShowPricingResponseDTO> response = service.getPricing();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }

    @PostMapping("/change-price")
    public ResponseEntity<?> changePrice(@RequestBody ChangePricingRequestDTO request) {
        try {
            service.changePrice(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }
}
