package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ReportDriverRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.IrregularityReport;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.IrregularityReportRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

@Service
public class IrregularityReportService {
    private final IrregularityReportRepository repository;
    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;

    public IrregularityReportService(
            IrregularityReportRepository repository,
            RideRepository rideRepository,
            PassengerRepository passengerRepository) {
        this.repository = repository;
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
    }

    public void reportDriver(ReportDriverRequestDTO dto) {
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found: " + dto.getRideId()));

        Passenger author = passengerRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Passenger not found: " + dto.getAuthorId()));

        IrregularityReport report = new IrregularityReport();
        report.setRide(ride);
        report.setAuthor(author);
        report.setDescription(dto.getComment());

        repository.save(report);
        repository.flush();
    }
}
