package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "panic_alerts")
public class PanicAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "triggered_by_user_id", nullable = false)
    private User triggeredBy;

    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    @Column(nullable = false)
    private String currentLocation;

    @Column(nullable = false)
    private boolean resolved = false;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedBy;

    private String resolutionNotes;

    public PanicAlert(Ride ride, User triggeredBy, String currentLocation) {
        this.ride = ride;
        this.triggeredBy = triggeredBy;
        this.currentLocation = currentLocation;
        this.triggeredAt = LocalDateTime.now();
        this.resolved = false;
    }
}