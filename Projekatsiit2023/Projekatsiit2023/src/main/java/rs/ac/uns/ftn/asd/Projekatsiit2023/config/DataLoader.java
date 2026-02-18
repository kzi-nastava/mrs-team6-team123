package rs.ac.uns.ftn.asd.Projekatsiit2023.config;

import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Pricing;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Vehicle;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PricingRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

@Configuration
public class DataLoader {

    @Bean
    @Transactional
    CommandLineRunner initData(PricingRepository pricingRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EntityManager entityManager) {
        return args -> {

            if (userRepository.count() > 0) {
                System.out.println("ℹ️ Data already exists, skipping initialization.");
                return;
            }

            if (pricingRepository.count() == 0) {
                if (pricingRepository.count() == 0) {
                    Pricing standard = new Pricing();
                    standard.setVehicleType(VehicleType.STANDARD);
                    standard.setPrice(300.0);
                    pricingRepository.save(standard);

                    Pricing luxury = new Pricing();
                    luxury.setVehicleType(VehicleType.LUXURY);
                    luxury.setPrice(500.0);
                    pricingRepository.save(luxury);

                    Pricing van = new Pricing();
                    van.setVehicleType(VehicleType.VAN);
                    van.setPrice(400.0);
                    pricingRepository.save(van);

                    System.out.println("✅ Pricing data loaded!");
                }

                // 2. TEST USERS
                System.out.println("📦 Loading test users...");

                // Passenger 1 - Lana
                Passenger user1 = new Passenger();
                user1.setEmail("user1@example.com");
                user1.setPassword(passwordEncoder.encode("user123")); // ENKRIPTOVANO!
                user1.setFirstName("Lana");
                user1.setLastName("One");
                user1.setAddress("Boulevard 1");
                user1.setPhone("060123456");
                user1.setUserRole(UserRole.PASSENGER);
                user1.setAccountActivated(true);
                user1.setAccountBlocked(false);
                user1.setStartedRide(false);
                userRepository.save(user1);

                // Passenger 2 - User Two
                Passenger user2 = new Passenger();
                user2.setEmail("user2@example.com");
                user2.setPassword(passwordEncoder.encode("user456"));
                user2.setFirstName("User");
                user2.setLastName("Two");
                user2.setAddress("Avenue 5");
                user2.setPhone("061987654");
                user2.setUserRole(UserRole.PASSENGER);
                user2.setAccountActivated(true);
                user2.setAccountBlocked(false);
                user2.setStartedRide(false);
                userRepository.save(user2);

                // Driver 1 - Voom Ana
                Driver driver1 = new Driver();
                driver1.setEmail("driver1@example.com");
                driver1.setPassword(passwordEncoder.encode("driver123"));
                driver1.setFirstName("Voom");
                driver1.setLastName("Ana");
                driver1.setAddress("Street 41");
                driver1.setPhone("061747474");
                driver1.setUserRole(UserRole.DRIVER);
                driver1.setAccountActivated(true);
                driver1.setAccountBlocked(false);
                driver1.setActive(true);
                driver1.setActiveMinutesLast24h(0);
                driver1.setTotalRides(0);
                driver1.setRating(0.0);

                // Vehicle za driver1
                Vehicle vehicle1 = new Vehicle();
                vehicle1.setVehicleModel("Toyota Corolla");
                vehicle1.setVehicleType(VehicleType.STANDARD);
                vehicle1.setLicensePlate("NS-123-AB");
                vehicle1.setSeats(4);
                vehicle1.setBabyTransport(true);
                vehicle1.setPetTransport(false);
                driver1.setVehicle(vehicle1);

                userRepository.save(driver1);

                // Driver 2
                Driver driver2 = new Driver();
                driver2.setEmail("driver2@example.com");
                driver2.setPassword(passwordEncoder.encode("driver456"));
                driver2.setFirstName("Driver");
                driver2.setLastName("Two");
                driver2.setAddress("Street 4");
                driver2.setPhone("069423751");
                driver2.setUserRole(UserRole.DRIVER);
                driver2.setAccountActivated(true);
                driver2.setAccountBlocked(false);
                driver2.setActive(true);
                driver2.setActiveMinutesLast24h(0);
                driver2.setTotalRides(0);
                driver2.setRating(0.0);

                Vehicle vehicle2 = new Vehicle();
                vehicle2.setVehicleModel("Mercedes E-Class");
                vehicle2.setVehicleType(VehicleType.LUXURY);
                vehicle2.setLicensePlate("NS-456-CD");
                vehicle2.setSeats(4);
                vehicle2.setBabyTransport(false);
                vehicle2.setPetTransport(true);
                driver2.setVehicle(vehicle2);

                userRepository.save(driver2);

                System.out.println("✅ Test users loaded!");
                System.out.println("📊 Total users in DB: " + userRepository.count());
            }
            ;
        };
    }

}