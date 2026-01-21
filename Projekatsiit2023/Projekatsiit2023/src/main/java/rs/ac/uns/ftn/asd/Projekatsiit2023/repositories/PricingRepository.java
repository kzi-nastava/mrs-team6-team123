package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Pricing;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Pricing findByVehicleType(VehicleType vehicleType);
}
