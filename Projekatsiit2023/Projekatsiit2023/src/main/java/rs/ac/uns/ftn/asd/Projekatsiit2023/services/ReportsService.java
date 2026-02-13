package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideDataPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StatisticsDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserBasicInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportsService {

    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;

    public ReportsService(RideRepository rideRepository, PassengerRepository passengerRepository,
            DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
    }

    /**
     * Get statistics for a specific user's rides (passenger or driver)
     */
    public StatisticsDTO getUserRideStatistics(Long userId, String userType, LocalDate fromDate, LocalDate toDate) {
        List<Ride> rides = new ArrayList<>();

        if ("PASSENGER".equalsIgnoreCase(userType)) {
            rides = rideRepository.findAll().stream()
                    .filter(r -> r.getCreator().getId().equals(userId) && r.getStatus() == RideStatus.FINISHED)
                    .filter(r -> !fromDate.isAfter(r.getDate()) && !toDate.isBefore(r.getDate()))
                    .toList();
        } else if ("DRIVER".equalsIgnoreCase(userType)) {
            rides = rideRepository.findAll().stream()
                    .filter(r -> r.getDriver().getId().equals(userId) && r.getStatus() == RideStatus.FINISHED)
                    .filter(r -> !fromDate.isAfter(r.getDate()) && !toDate.isBefore(r.getDate()))
                    .toList();
        }

        return calculateStatistics(rides);
    }

    public StatisticsDTO getUserRideStatisticsByIdAndType(Long userId, String userType, LocalDate fromDate,
            LocalDate toDate) {
        return getUserRideStatistics(userId, userType, fromDate, toDate);
    }

    public StatisticsDTO getAllFinishedRidesStatistics(LocalDate fromDate, LocalDate toDate) {
        List<Ride> rides = rideRepository.findByStatus(RideStatus.FINISHED).stream()
                .filter(r -> !fromDate.isAfter(r.getDate()) && !toDate.isBefore(r.getDate()))
                .toList();
        return calculateStatistics(rides);
    }

    public List<UserBasicInfoDTO> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .map(p -> new UserBasicInfoDTO(
                        p.getId(),
                        p.getEmail(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getUserRole()))
                .toList();
    }

    public List<UserBasicInfoDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(d -> new UserBasicInfoDTO(
                        d.getId(),
                        d.getEmail(),
                        d.getFirstName(),
                        d.getLastName(),
                        d.getUserRole()))
                .toList();
    }

    public List<UserBasicInfoDTO> getAllUsers() {
        List<UserBasicInfoDTO> users = new ArrayList<>();
        users.addAll(getAllPassengers());
        users.addAll(getAllDrivers());
        return users;
    }

    public List<UserBasicInfoDTO> getAllActiveUsers(Long excludeUserId) {
        return getAllUsers().stream()
                .filter(u -> !u.getId().equals(excludeUserId))
                .toList();
    }

    private StatisticsDTO calculateStatistics(List<Ride> rides) {
        StatisticsDTO stats = new StatisticsDTO();

        if (rides.isEmpty()) {
            stats.setTotalRides(0L);
            stats.setAvgRidesPerDay(0.0);
            stats.setRidesData(new ArrayList<>());
            stats.setTotalKmTraveled(0.0);
            stats.setAvgKmPerDay(0.0);
            stats.setKmData(new ArrayList<>());
            stats.setTotalAmountSpent(0.0);
            stats.setAvgAmountPerDay(0.0);
            stats.setAmountData(new ArrayList<>());
            return stats;
        }

        // Calculate totals
        double totalKm = rides.stream().mapToDouble(Ride::getTotalDistance).sum();
        double totalAmount = rides.stream().mapToDouble(Ride::getPrice).sum();
        long totalRidesCount = rides.size();

        // Group by date
        Map<LocalDate, List<Ride>> ridesByDate = new HashMap<>();
        for (Ride ride : rides) {
            ridesByDate.computeIfAbsent(ride.getDate(), k -> new ArrayList<>()).add(ride);
        }

        // Create data points for each metric
        List<RideDataPointDTO> ridesData = new ArrayList<>();
        List<RideDataPointDTO> kmData = new ArrayList<>();
        List<RideDataPointDTO> amountData = new ArrayList<>();

        ridesByDate.forEach((date, dateRides) -> {
            ridesData.add(new RideDataPointDTO(date, (double) dateRides.size()));
            kmData.add(new RideDataPointDTO(date, dateRides.stream().mapToDouble(Ride::getTotalDistance).sum()));
            amountData.add(new RideDataPointDTO(date, dateRides.stream().mapToDouble(Ride::getPrice).sum()));
        });

        // Sort by date
        ridesData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        kmData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        amountData.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // Set statistics
        stats.setTotalRides(totalRidesCount);
        stats.setAvgRidesPerDay(totalRidesCount / (double) ridesByDate.size());
        stats.setRidesData(ridesData);

        stats.setTotalKmTraveled(totalKm);
        stats.setAvgKmPerDay(totalKm / ridesByDate.size());
        stats.setKmData(kmData);

        stats.setTotalAmountSpent(totalAmount);
        stats.setAvgAmountPerDay(totalAmount / ridesByDate.size());
        stats.setAmountData(amountData);

        return stats;
    }
}
