package rs.ac.uns.ftn.asd.Projekatsiit2023.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "passengers")
public class Passenger extends User {
    @Column(nullable = false)
    private boolean startedRide;
}
