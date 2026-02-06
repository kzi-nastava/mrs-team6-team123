package com.example.mobile_application.repository;

import com.example.mobile_application.model.DriverRideHistoryDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.DriverRideHistoryService;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DriverRideHistoryRepository {
    private final DriverRideHistoryService api;

    public DriverRideHistoryRepository() {
        api = ApiClient.getInstance().create(DriverRideHistoryService.class);
    }

    public void getDriverRideHistory(
            Long driverId,
            LocalDate from,
            LocalDate to,
            Callback<List<DriverRideHistoryDTO>> callback) {
        Call<List<DriverRideHistoryDTO>> call = api.getDriverRideHistory(driverId, from, to);
        call.enqueue(callback);
    }
}
