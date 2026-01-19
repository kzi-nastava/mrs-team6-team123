package rs.ac.uns.ftn.asd.Projekatsiit2023.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor

@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rideId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // ManyToOne relationship with Passenger entity
    // Passenger entity extends User entity
    private Long creatorId;

    // ManyToMany relationship with Passenger entity
    private List<Long> passengerIds;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double totalDistance;

    @Column(nullable = false)
    private boolean panicTriggered;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "canceled_user_id")
    private User user;

    // ManyToOne relationship with Route entity
    private Long routeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;
}
