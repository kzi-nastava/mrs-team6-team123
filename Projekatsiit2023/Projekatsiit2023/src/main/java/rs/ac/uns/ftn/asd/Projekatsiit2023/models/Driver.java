package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.DriverStatus;

@Setter
@Getter
@NoArgsConstructor

@Entity
@Table(name = "drivers")
public class Driver extends User {
    @Column(nullable = false)
    private boolean active; 

    @Column(nullable = false)
    private int activeMinutesLast24h;

    @Column(nullable = false)
    private int totalRides = 0;

    @Column(nullable = false)
    private double rating = 0.0;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", unique = true)
    private Vehicle vehicle;
}
