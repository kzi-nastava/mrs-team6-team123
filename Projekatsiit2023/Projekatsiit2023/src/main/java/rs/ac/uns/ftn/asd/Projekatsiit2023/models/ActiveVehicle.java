package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "active_vehicles")
public class ActiveVehicle {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private double currentLatitude;

    @Column(nullable = false)
    private double currentLongitude;

    @Column
    private double targetLatitude;

    @Column
    private double targetLongitude;

    @Column(nullable = false)
    private boolean available;

    @Column(columnDefinition = "TEXT")
    private String routeCoordinates;

    @Column(nullable = false)
    private int routeIndex = 0;
}
