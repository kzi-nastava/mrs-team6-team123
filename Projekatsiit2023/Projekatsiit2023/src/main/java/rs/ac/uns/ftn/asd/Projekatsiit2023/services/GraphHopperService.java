package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class GraphHopperService {

    private final RestTemplate restTemplate;
    private final String apiKey = "aaf786fc-24d2-4532-8c84-15ac88b63184";

    public GraphHopperService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getRoute(List<GeoPointDTO> points) {
        StringBuilder url = new StringBuilder(
                "https://graphhopper.com/api/1/route?vehicle=car&points_encoded=false&key=" + apiKey
        );

        for (GeoPointDTO p : points) {
            url.append("&point=").append(p.getLatitude()).append(",").append(p.getLongitude());
        }

        return restTemplate.getForEntity(url.toString(), String.class);
    }

    public List<GeoPointDTO> getRoutePoints(List<GeoPointDTO> points) {
        ResponseEntity<String> response = getRoute(points);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode coordinates = root.at("/paths/0/points/coordinates");

            List<GeoPointDTO> routePoints = new ArrayList<>();
            for (JsonNode coord : coordinates) {
                double lon = coord.get(0).asDouble();
                double lat = coord.get(1).asDouble();
                GeoPointDTO point = new GeoPointDTO();
                point.setLatitude(lat);
                point.setLongitude(lon);
                point.setLocation("");
                routePoints.add(point);
            }
            return routePoints;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GraphHopper response", e);
        }
    }
}
