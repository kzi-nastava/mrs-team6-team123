package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor

@Entity
@Table(name = "rides")
public class Ride {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "driver_id", nullable = false)
        private Driver driver;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "creator_id", nullable = false)
        private Passenger creator;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "ride_passengers", joinColumns = @JoinColumn(name = "ride_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
        private List<Passenger> passengers = new ArrayList<>();

        @Column(nullable = false)
        private String startLocation;

        @Column
        private String endLocation;

        @Column
        private LocalDateTime scheduledAt;

        @Column
        private LocalDateTime startedAt;

        @Column
        private LocalTime endedAt;

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false)
        private double price;

        @Column
        private Integer estimatedDurationMinutes; // Duration of this ride in minutes

        @Column(nullable = false)
        private boolean isPaid = false;

        @Column(nullable = false)
        private double totalDistance;

        @Column(nullable = false)
        private boolean panicTriggered;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "canceled_user_id")
        private User canceledBy;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "route_id", nullable = false)
        private Route route;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private RideStatus status;

        @Column(nullable = false)
        private boolean rideRated;

        @Column(nullable = false)
        private boolean driverReported;

        @Column(nullable = false)
        private boolean rideStopped;

        @Column
        private double driverRating;

        @Column
        private double vehicleRating;

        @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        private List<IrregularityReport> irregularityReports = new ArrayList<>();

        @Column(nullable = false)
        private int stopsMade = 0;

        @Column
        private double endLatitude;

        @Column
        private double endLongitude;

        // Track if reminder notifications have been sent
        @Column(nullable = false)
        private boolean notified15Min = false;

        @Column(nullable = false)
        private boolean notified10Min = false;

        @Column(nullable = false)
        private boolean notified5Min = false;
}