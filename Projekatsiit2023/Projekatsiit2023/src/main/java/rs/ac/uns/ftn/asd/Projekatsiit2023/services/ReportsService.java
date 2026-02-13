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
import java.util.*;
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

    public StatisticsDTO getUserRideStatistics(Long userId, String userType, LocalDate startDate, LocalDate endDate) {
        if ("DRIVER".equalsIgnoreCase(userType)) {
            return getDriverRideStatistics(userId, startDate, endDate);
        } else {
            return getPassengerRideStatistics(userId, startDate, endDate);
        }
    }

    public StatisticsDTO getPassengerRideStatistics(Long passengerId, LocalDate startDate, LocalDate endDate) {
        // Verify passenger exists
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + passengerId));

        // Get all finished rides for this passenger
        List<Ride> finishedRides = rideRepository.findFinishedRidesByPassengerId(passengerId);

        // Apply date filter if provided
        finishedRides = filterByDateRange(finishedRides, startDate, endDate);

        return buildStatistics(finishedRides, startDate, endDate);
    }

    public StatisticsDTO getDriverRideStatistics(Long driverId, LocalDate startDate, LocalDate endDate) {
        // Verify driver exists
        driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Get all finished rides for this driver
        List<Ride> finishedRides = rideRepository.findFinishedRidesByDriverId(driverId);

        // Apply date filter if provided
        finishedRides = filterByDateRange(finishedRides, startDate, endDate);

        return buildStatistics(finishedRides, startDate, endDate);
    }

    public StatisticsDTO getAllFinishedRidesStatistics(LocalDate startDate, LocalDate endDate) {
        // Get all finished rides from database (for admin)
        List<Ride> finishedRides = rideRepository.findAllFinishedRides();

        // Apply date filter if provided
        finishedRides = filterByDateRange(finishedRides, startDate, endDate);

        return buildStatistics(finishedRides, startDate, endDate);
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

    private double calculateAverageRidesPerDay(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        if (rides.isEmpty()) {
            return 0.0;
        }

        // Count unique dates from rides
        Set<LocalDate> uniqueDates = rides.stream()
                .map(Ride::getDate)
                .collect(Collectors.toSet());

        if (uniqueDates.isEmpty()) {
            return 0.0;
        }

        return (double) rides.size() / uniqueDates.size();
    }

    private double calculateAverageKmPerDay(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        if (rides.isEmpty()) {
            return 0.0;
        }

        double totalKm = rides.stream()
                .mapToDouble(Ride::getTotalDistance)
                .sum();

        // Count unique dates from rides
        Set<LocalDate> uniqueDates = rides.stream()
                .map(Ride::getDate)
                .collect(Collectors.toSet());

        if (uniqueDates.isEmpty()) {
            return 0.0;
        }

        return totalKm / uniqueDates.size();
    }

    private double calculateAverageAmountPerDay(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        if (rides.isEmpty()) {
            return 0.0;
        }

        double totalAmount = rides.stream()
                .mapToDouble(Ride::getPrice)
                .sum();

        // Count unique dates from rides
        Set<LocalDate> uniqueDates = rides.stream()
                .map(Ride::getDate)
                .collect(Collectors.toSet());

        if (uniqueDates.isEmpty()) {
            return 0.0;
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

        // Sort by role (DRIVER first, then PASSENGER) and then by email
        allUsers.sort(Comparator.comparing(UserBasicInfoDTO::getUserRole)
                .thenComparing(UserBasicInfoDTO::getEmail));

        return allUsers;
    }

    public StatisticsDTO getUserRideStatisticsByIdAndType(Long userId, String userType, LocalDate startDate,
            LocalDate endDate) {
        if ("DRIVER".equalsIgnoreCase(userType)) {
            return getDriverRideStatistics(userId, startDate, endDate);
        } else {
            return getPassengerRideStatistics(userId, startDate, endDate);
        }
    }

    /**
     * Filter rides by date range
     */
    private List<Ride> filterByDateRange(List<Ride> rides, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return rides;
        }

        return rides.stream()
                .filter(ride -> {
                    LocalDate rideDate = ride.getDate();
                    if (startDate != null && rideDate.isBefore(startDate)) {
                        return false;
                    }
                    if (endDate != null && rideDate.isAfter(endDate)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
