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

    private StatisticsDTO buildStatistics(List<Ride> finishedRides, LocalDate startDate, LocalDate endDate) {
        // Calculate statistics
        StatisticsDTO statistics = new StatisticsDTO();

        // Rides Statistics
        statistics.setTotalRides((long )finishedRides.size());
        statistics.setAvgRidesPerDay(calculateAverageRidesPerDay(finishedRides, startDate, endDate));
        statistics.setRidesData(groupRidesByDate(finishedRides, startDate, endDate));

        // Kilometers Statistics
        double totalKm = finishedRides.stream()
                .mapToDouble(Ride::getTotalDistance)
                .sum();
        statistics.setTotalKmTraveled(totalKm);
        statistics.setAvgKmPerDay(calculateAverageKmPerDay(finishedRides, startDate, endDate));
        statistics.setKmData(groupKmByDate(finishedRides, startDate, endDate));

        // Amount Spent Statistics
        double totalAmount = finishedRides.stream()
                .mapToDouble(Ride::getPrice)
                .sum();
        statistics.setTotalAmountSpent(totalAmount);
        statistics.setAvgAmountPerDay(calculateAverageAmountPerDay(finishedRides, startDate, endDate));
        statistics.setAmountData(groupAmountByDate(finishedRides, startDate, endDate));

        return statistics;
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

        return totalAmount / uniqueDates.size();
    }

    private List<RideDataPointDTO> groupRidesByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.counting()));

        return sortDataByDate(ridesByDate, startDate, endDate);
    }

    private List<RideDataPointDTO> groupKmByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> kmByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.summingDouble(Ride::getTotalDistance)));

        return sortDataByDate(kmByDate, startDate, endDate);
    }

    private List<RideDataPointDTO> groupAmountByDate(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> amountByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        Ride::getDate,
                        Collectors.summingDouble(Ride::getPrice)));

        return sortDataByDate(amountByDate, startDate, endDate);
    }

    private List<RideDataPointDTO> sortDataByDate(Map<LocalDate, ? extends Number> dataByDate, LocalDate startDate,
            LocalDate endDate) {
        // Return data sorted by date
        return dataByDate.entrySet().stream()
                .map(entry -> new RideDataPointDTO(entry.getKey(), entry.getValue().doubleValue()))
                .sorted(Comparator.comparing(RideDataPointDTO::getDate))
                .collect(Collectors.toList());
    }

    public List<UserBasicInfoDTO> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .filter(p -> p.isAccountActivated()) // Only activated accounts
                .map(p -> new UserBasicInfoDTO(p.getId(), p.getEmail(), p.getFirstName(), p.getLastName(), UserRole.PASSENGER))
                .collect(Collectors.toList());
    }

    public List<UserBasicInfoDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .filter(d -> d.isAccountActivated()) // Only activated accounts
                .map(d -> new UserBasicInfoDTO(d.getId(), d.getEmail(), d.getFirstName(), d.getLastName(), UserRole.DRIVER))
                .collect(Collectors.toList());
    }

    public List<UserBasicInfoDTO> getAllActiveUsers(Long excludeUserId) {
        List<UserBasicInfoDTO> allUsers = new ArrayList<>();

        // Add all active passengers
        allUsers.addAll(passengerRepository.findAll().stream()
                .filter(p -> p.isAccountActivated() && !p.getId().equals(excludeUserId))
                .map(p -> new UserBasicInfoDTO(p.getId(), p.getEmail(), p.getFirstName(), p.getLastName(), UserRole.PASSENGER))
                .collect(Collectors.toList()));

        // Add all active drivers
        allUsers.addAll(driverRepository.findAll().stream()
                .filter(d -> d.isAccountActivated() && !d.getId().equals(excludeUserId))
                .map(d -> new UserBasicInfoDTO(d.getId(), d.getEmail(), d.getFirstName(), d.getLastName(), UserRole.DRIVER))
                .collect(Collectors.toList()));

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
