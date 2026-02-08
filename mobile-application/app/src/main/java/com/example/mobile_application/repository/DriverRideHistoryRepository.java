package com.example.mobile_application.repository;

import com.example.mobile_application.dto.DriverRideHistoryDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.DriverRideHistoryService;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DriverRideHistoryRepository {
    private final DriverRideHistoryService service;

    public DriverRideHistoryRepository() {
        service = ApiClient.getInstance().create(DriverRideHistoryService.class);
    }

    public void getDriverRideHistory(
            Long driverId,
            LocalDate from,
            LocalDate to,
            Callback<List<DriverRideHistoryDTO>> callback) {
        Call<List<DriverRideHistoryDTO>> call = service.getDriverRideHistory(driverId, from, to);
        call.enqueue(callback);
    }
}
