package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @Column(name = "vehicle_id")
    private Long id;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private double currentLatitude;

    @Column(nullable = false)
    private double currentLongitude;

    @Column
    private double targetLatitude = 0.0;

    @Column
    private double targetLongitude = 0.0;

    @Column(nullable = false)
    private boolean available;

    @Column(columnDefinition = "TEXT")
    private String routeCoordinates;

    @Column(nullable = false)
    private int routeIndex = 0;

    @OneToOne
    @JoinColumn(name = "current_ride_id", nullable = true)
    private Ride currentRide;
}
