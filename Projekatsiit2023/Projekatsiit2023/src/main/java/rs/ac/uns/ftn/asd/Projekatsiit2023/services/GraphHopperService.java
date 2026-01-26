package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.ride.GeoPointDTO;

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
}
