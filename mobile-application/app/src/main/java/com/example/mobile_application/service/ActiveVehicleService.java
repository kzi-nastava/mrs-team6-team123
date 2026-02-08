package com.example.mobile_application.service;

import com.example.mobile_application.dto.ActiveVehicleDTO;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ActiveVehicleService {
    @GET("api/public-map/active")
    Call<List<ActiveVehicleDTO>> getActiveVehicles();

    @GET("api/public-map/active/{driverId}")
    Call<ActiveVehicleDTO> getDriversVehicle(@Path("driverId") Long driverId);
}
