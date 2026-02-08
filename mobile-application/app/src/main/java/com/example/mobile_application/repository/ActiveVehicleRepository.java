package com.example.mobile_application.repository;

import com.example.mobile_application.dto.ActiveVehicleDTO;
import com.example.mobile_application.service.ActiveVehicleService;
import com.example.mobile_application.service.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ActiveVehicleRepository {
    private final ActiveVehicleService service;

    public ActiveVehicleRepository() {
        this.service = ApiClient.getInstance().create(ActiveVehicleService.class);
    }

    public void getActiveVehicles(Callback<List<ActiveVehicleDTO>> callback) {
        Call<List<ActiveVehicleDTO>> call = service.getActiveVehicles();
        call.enqueue(callback);
    }

    public void getDriversVehicle(Long driverId, Callback<ActiveVehicleDTO> callback) {
        Call<ActiveVehicleDTO> call = service.getDriversVehicle(driverId);
        call.enqueue(callback);
    }
}
