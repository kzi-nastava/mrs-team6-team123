package com.example.mobile_application.service;

import com.example.mobile_application.dto.FavoriteRouteDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FavoritesService {
    @GET("/api/passenger/{passengerId}/favorite-routes")
    Call<List<FavoriteRouteDTO>> getFavoriteRoutes(@Path("passengerId") Long passengerId);

    @POST("/api/passenger/{passengerId}/favorite-routes")
    Call<Void> addFavoriteRoute(
            @Path("passengerId") Long passengerId,
            @Query("routeId") Long routeId);

    @DELETE("/api/passenger/{passengerId}/favorite-routes/{favoriteRouteId}")
    Call<Void> removeFavoriteRoute(
            @Path("passengerId") Long passengerId,
            @Path("favoriteRouteId") Long favoriteRouteId);
}
