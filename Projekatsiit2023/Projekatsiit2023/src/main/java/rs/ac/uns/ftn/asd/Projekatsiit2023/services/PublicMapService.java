package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.driver.ActiveVehicleDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicMapService {
    private final Map<Long, ActiveVehicleDTO> vehicles = new HashMap<>();

    public PublicMapService() {
        vehicles.put(1L, new ActiveVehicleDTO(1L, 45.2671, 19.8335, true));
        vehicles.put(2L, new ActiveVehicleDTO(2L, 45.2450, 19.8300, false));
        vehicles.put(3L, new ActiveVehicleDTO(3L, 45.2500, 19.8250, true));
        vehicles.put(4L, new ActiveVehicleDTO(4L, 45.2553, 19.8480, false));
        vehicles.put(5L, new ActiveVehicleDTO(5L, 45.2472, 19.8489, false));
        vehicles.put(6L, new ActiveVehicleDTO(6L, 45.2405, 19.8219, true));
        vehicles.put(7L, new ActiveVehicleDTO(7L, 45.2455, 19.8406, false));
        vehicles.put(8L, new ActiveVehicleDTO(8L, 45.2431, 19.8475, true));
        vehicles.put(9L, new ActiveVehicleDTO(9L, 45.2302, 19.8089, true));
        vehicles.put(10L, new ActiveVehicleDTO(10L, 45.2476, 19.7994, false));
        vehicles.put(11L, new ActiveVehicleDTO(11L, 45.2528, 19.8030, false));
    }

    public List<ActiveVehicleDTO> getVehicles() {
        vehicles.values().forEach(v -> {
            v.setLatitude(v.getLatitude() + (Math.random() - 0.5) * 0.0005);
            v.setLongitude(v.getLongitude() + (Math.random() - 0.5) * 0.0005);
        });
        return new ArrayList<>(vehicles.values());
    }
}
