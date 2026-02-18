package com.example.mobile_application.repository;

import com.example.mobile_application.dto.FavoriteRouteDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.FavoritesService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FavoritesRepository {
    private final FavoritesService service;

    public FavoritesRepository() {
        this.service = ApiClient.getInstance().create(FavoritesService.class);
    }

    public void getFavoriteRoutes(Long passengerId, Callback<List<FavoriteRouteDTO>> callback) {
        Call<List<FavoriteRouteDTO>> call = service.getFavoriteRoutes(passengerId);
        call.enqueue(callback);
    }

    public void addFavoriteRoute(Long passengerId, Long routeId, Callback<Void> callback) {
        Call<Void> call = service.addFavoriteRoute(passengerId, routeId);
        call.enqueue(callback);
    }

    public void removeFavoriteRoute(Long passengerId, Long favoriteRouteId, Callback<Void> callback) {
        Call<Void> call = service.removeFavoriteRoute(passengerId, favoriteRouteId);
        call.enqueue(callback);
    }
}
