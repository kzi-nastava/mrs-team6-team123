package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.RideEstimationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Pricing;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PricingRepository;

import java.util.List;
import java.util.Map;

@Service
public class RideEstimationService {

    private final PricingRepository pricingRepository;
    private final RestTemplate restTemplate;
    private static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving/";
    private static final double PRICE_PER_KM = 120.0;

    public RideEstimationService(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
        this.restTemplate = new RestTemplate();
    }

    public RideEstimationResponseDTO estimate(RideEstimationRequestDTO request) {
        if (request.getStartLocation() == null || request.getStartLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Start location is required");
        }
        if (request.getEndLocation() == null || request.getEndLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("End location is required");
        }
        if (request.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        double[] startCoords = parseLocation(request.getStartLocation());
        double[] endCoords = parseLocation(request.getEndLocation());

        StringBuilder coords = new StringBuilder();
        coords.append(startCoords[1]).append(",").append(startCoords[0]);

        if (request.getIntermediateStops() != null && !request.getIntermediateStops().isEmpty()) {
            for (String stop : request.getIntermediateStops()) {
                double[] stopCoords = parseLocation(stop);
                coords.append(";").append(stopCoords[1]).append(",").append(stopCoords[0]);
            }
        }

        coords.append(";").append(endCoords[1]).append(",").append(endCoords[0]);

        String url = OSRM_URL + coords.toString() + "?overview=full&geometries=geojson";

        double distanceKm;
        int estimatedTimeMin;
        String routeGeometry = "";

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "Ok".equals(response.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");

                if (routes != null && !routes.isEmpty()) {
                    Map<String, Object> route = routes.get(0);

                    double distanceMeters = ((Number) route.get("distance")).doubleValue();
                    distanceKm = distanceMeters / 1000.0;

                    double durationSeconds = ((Number) route.get("duration")).doubleValue();
                    estimatedTimeMin = (int) Math.ceil(durationSeconds / 60.0);

                    Map<String, Object> geometry = (Map<String, Object>) route.get("geometry");
                    if (geometry != null) {
                        routeGeometry = geometry.toString();
                    }
                } else {
                    throw new RuntimeException("No route found");
                }
            } else {
                throw new RuntimeException("OSRM returned error: " + response);
            }
        } catch (Exception e) {
            System.err.println("OSRM Error, using fallback: " + e.getMessage());
            distanceKm = calculateHaversineDistance(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);
            estimatedTimeMin = (int) Math.ceil((distanceKm / 30.0) * 60);
            routeGeometry = "Fallback route (straight line)";
        }

        Pricing pricing = pricingRepository.findByVehicleType(request.getVehicleType());
        if (pricing == null) {
            throw new RuntimeException("Pricing not found for vehicle type: " + request.getVehicleType());
        }

        double estimatedPrice = pricing.getPrice() + (distanceKm * PRICE_PER_KM);
        estimatedPrice = Math.round(estimatedPrice * 100.0) / 100.0;
        distanceKm = Math.round(distanceKm * 100.0) / 100.0;

        RideEstimationResponseDTO responseDTO = new RideEstimationResponseDTO();
        responseDTO.setStartLocation(request.getStartLocation());
        responseDTO.setEndLocation(request.getEndLocation());
        responseDTO.setEstimatedDistance(distanceKm);
        responseDTO.setEstimatedTime(estimatedTimeMin);
        responseDTO.setEstimatedPrice(estimatedPrice);
        responseDTO.setRoute(routeGeometry);

        return responseDTO;
    }

    private double[] parseLocation(String location) {
        try {
            String cleaned = location.replaceAll("[NSEW]", "").trim();
            String[] parts = cleaned.split(",");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid location format: " + location);
            }

            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                throw new IllegalArgumentException("Coordinates out of range");
            }

            return new double[] { lat, lon };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate format: " + location);
        }
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}