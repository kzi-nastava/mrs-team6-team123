package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.FavoriteRouteDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Passenger;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PassengerFavoriteRoute;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Route;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passenger/{passengerId}")
public class FavoriteRoutesController {

    private final PassengerRepository passengerRepository;
    private final RouteRepository routeRepository;

    public FavoriteRoutesController(PassengerRepository passengerRepository, RouteRepository routeRepository) {
        this.passengerRepository = passengerRepository;
        this.routeRepository = routeRepository;
    }

    @GetMapping("/favorite-routes")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getFavoriteRoutes(@PathVariable Long passengerId) {
        try {
            Passenger passenger = passengerRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Passenger not found"));

            List<FavoriteRouteDTO> favorites = passenger.getFavoriteRoutes().stream()
                    .map(fav -> {
                        Route route = fav.getRoute();
                        return new FavoriteRouteDTO(
                                fav.getId(),
                                route.getId(),
                                route.getStartLocation(),
                                route.getEndLocation(),
                                route.getStartLatitude(),
                                route.getStartLongitude(),
                                route.getEndLatitude(),
                                route.getEndLongitude(),
                                fav.getCreatedAt());
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/favorite-routes")
    @Transactional
    public ResponseEntity<?> addFavoriteRoute(
            @PathVariable Long passengerId,
            @RequestParam Long routeId) {
        try {
            Passenger passenger = passengerRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Passenger not found"));

            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new RuntimeException("Route not found"));

            // Check if route is already favorited
            boolean alreadyFavorited = passenger.getFavoriteRoutes().stream()
                    .anyMatch(fav -> fav.getRoute().getId().equals(routeId));

            if (alreadyFavorited) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Route already favorited");
            }

            // Create new favorite route
            PassengerFavoriteRoute favorite = new PassengerFavoriteRoute();
            favorite.setPassenger(passenger);
            favorite.setRoute(route);
            favorite.setCreatedAt(java.time.LocalDateTime.now());

            passenger.getFavoriteRoutes().add(favorite);
            passengerRepository.save(passenger);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/favorite-routes/{favoriteRouteId}")
    @Transactional
    public ResponseEntity<?> removeFavoriteRoute(
            @PathVariable Long passengerId,
            @PathVariable Long favoriteRouteId) {
        try {
            Passenger passenger = passengerRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Passenger not found"));

            passenger.getFavoriteRoutes().removeIf(fav -> fav.getId().equals(favoriteRouteId));
            passengerRepository.save(passenger);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
