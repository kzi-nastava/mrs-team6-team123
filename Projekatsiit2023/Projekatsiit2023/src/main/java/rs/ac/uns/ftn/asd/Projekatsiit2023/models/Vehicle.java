package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleModel;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private boolean babyTransport;

    @Column(nullable = false)
    private boolean petTransport;
}
