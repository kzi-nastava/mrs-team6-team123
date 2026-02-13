package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideDataPointDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.StatisticsDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.user.UserBasicInfoDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.RideStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        return buildStatistics(rides, fromDate, toDate);
    }

    /**
     * Alternative method name for getting user statistics
     */
    public StatisticsDTO getUserRideStatisticsByIdAndType(Long userId, String userType, LocalDate fromDate,
            LocalDate toDate) {
        return getUserRideStatistics(userId, userType, fromDate, toDate);
    }

    /**
     * Get statistics for a passenger (used by old endpoint)
     */
    public StatisticsDTO getPassengerRideStatistics(Long passengerId, LocalDate fromDate, LocalDate toDate) {
        // Use current date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusMonths(1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        return getUserRideStatistics(passengerId, "PASSENGER", fromDate, toDate);
    }

    /**
     * Get all finished rides statistics (for admin)
     */
    public StatisticsDTO getAllFinishedRidesStatistics(LocalDate fromDate, LocalDate toDate) {
        List<Ride> rides = rideRepository.findByStatus(RideStatus.FINISHED).stream()
                .filter(r -> !fromDate.isAfter(r.getDate()) && !toDate.isBefore(r.getDate()))
                .toList();
        return buildStatistics(rides, fromDate, toDate);
    }

    /**
     * Build statistics from a list of rides
     */
    private StatisticsDTO buildStatistics(List<Ride> finishedRides, LocalDate startDate, LocalDate endDate) {
        StatisticsDTO statistics = new StatisticsDTO();

        // Handle empty case
        if (finishedRides.isEmpty()) {
            statistics.setTotalRides(0L);
            statistics.setAvgRidesPerDay(0.0);
            statistics.setRidesData(new ArrayList<>());
            statistics.setTotalKmTraveled(0.0);
            statistics.setAvgKmPerDay(0.0);
            statistics.setKmData(new ArrayList<>());
            statistics.setTotalAmountSpent(0.0);
            statistics.setAvgAmountPerDay(0.0);
            statistics.setAmountData(new ArrayList<>());
            return statistics;
        }

        // Calculate total days in range
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Rides Statistics
        statistics.setTotalRides((long) finishedRides.size());
        statistics.setAvgRidesPerDay(finishedRides.size() / (double) daysBetween);
        statistics.setRidesData(groupRidesByDate(finishedRides, startDate, endDate));

        // Kilometers Statistics
        double totalKm = finishedRides.stream()
                .mapToDouble(Ride::getTotalDistance)
                .sum();
        statistics.setTotalKmTraveled(totalKm);
        statistics.setAvgKmPerDay(totalKm / daysBetween);
        statistics.setKmData(groupKmByDate(finishedRides, startDate, endDate));

        // Amount Spent Statistics
        double totalAmount = finishedRides.stream()
                .mapToDouble(Ride::getPrice)
                .sum();
        statistics.setTotalAmountSpent(totalAmount);
        statistics.setAvgAmountPerDay(totalAmount / daysBetween);
        statistics.setAmountData(groupAmountByDate(finishedRides, startDate, endDate));

        return statistics;
    }

    /**
     * Group rides by date for chart display
     */
    private List<RideDataPointDTO> groupRidesByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.counting()));

        return sortDataByDate(ridesByDate, startDate, endDate);
    }

    /**
     * Group kilometers by date for chart display
     */
    private List<RideDataPointDTO> groupKmByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> kmByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.summingDouble(Ride::getTotalDistance)));

        return sortDataByDate(kmByDate, startDate, endDate);
    }

    /**
     * Group amounts by date for chart display
     */
    private List<RideDataPointDTO> groupAmountByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> amountByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.summingDouble(Ride::getPrice)));

        return sortDataByDate(amountByDate, startDate, endDate);
    }

    /**
     * Sort data by date (only includes dates with actual data)
     */
    private List<RideDataPointDTO> sortDataByDate(Map<LocalDate, ? extends Number> dataByDate, LocalDate startDate,
            LocalDate endDate) {
        // Only include dates that have actual data (no zero values)
        return dataByDate.entrySet().stream()
                .map(entry -> new RideDataPointDTO(entry.getKey(), entry.getValue().doubleValue()))
                .sorted(Comparator.comparing(RideDataPointDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Get all active passengers
     */
    public List<UserBasicInfoDTO> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .filter(p -> p.isAccountActivated())
                .map(p -> new UserBasicInfoDTO(p.getId(), p.getEmail(), p.getFirstName(), p.getLastName(),
                        UserRole.PASSENGER))
                .collect(Collectors.toList());
    }

    /**
     * Get all active drivers
     */
    public List<UserBasicInfoDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .filter(d -> d.isAccountActivated())
                .map(d -> new UserBasicInfoDTO(d.getId(), d.getEmail(), d.getFirstName(), d.getLastName(),
                        UserRole.DRIVER))
                .collect(Collectors.toList());
    }

    /**
     * Get all active users (both passengers and drivers), excluding one user
     */
    public List<UserBasicInfoDTO> getAllActiveUsers(Long excludeUserId) {
        List<UserBasicInfoDTO> allUsers = new ArrayList<>();

        // Add all active passengers except the excluded one
        allUsers.addAll(passengerRepository.findAll().stream()
                .filter(p -> p.isAccountActivated() && !p.getId().equals(excludeUserId))
                .map(p -> new UserBasicInfoDTO(p.getId(), p.getEmail(), p.getFirstName(), p.getLastName(),
                        UserRole.PASSENGER))
                .collect(Collectors.toList()));

        // Add all active drivers except the excluded one
        allUsers.addAll(driverRepository.findAll().stream()
                .filter(d -> d.isAccountActivated() && !d.getId().equals(excludeUserId))
                .map(d -> new UserBasicInfoDTO(d.getId(), d.getEmail(), d.getFirstName(), d.getLastName(),
                        UserRole.DRIVER))
                .collect(Collectors.toList()));

        return allUsers;
    }
}
