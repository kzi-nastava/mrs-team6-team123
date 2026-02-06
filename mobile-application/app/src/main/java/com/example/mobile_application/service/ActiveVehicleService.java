package com.example.mobile_application.service;

import com.example.mobile_application.dto.ActiveVehicleDTO;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ActiveVehicleService {
    @GET("api/public-map/active")
    Call<List<ActiveVehicleDTO>> getActiveVehicles();
}
