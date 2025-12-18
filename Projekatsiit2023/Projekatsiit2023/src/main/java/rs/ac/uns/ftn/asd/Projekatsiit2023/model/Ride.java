package rs.ac.uns.ftn.asd.Projekatsiit2023.model;

import java.time.LocalDateTime;

public class Ride {

    private Long id;
    private User driver;
    private User passenger;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private String status; // CREATED, STARTED, FINISHED, CANCELED

    public Ride() {}
}
