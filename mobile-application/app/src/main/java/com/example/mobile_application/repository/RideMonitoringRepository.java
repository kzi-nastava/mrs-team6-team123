package com.example.mobile_application.repository;

import com.example.mobile_application.dto.RideMonitoringDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideMonitoringService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RideMonitoringRepository {
    private final RideMonitoringService service;

    public RideMonitoringRepository() {
        service = ApiClient.getInstance().create(RideMonitoringService.class);
    }

    public void getAllActiveRides(Callback<List<RideMonitoringDTO>> callback) {
        Call<List<RideMonitoringDTO>> call = service.getAllActiveRides();
        call.enqueue(callback);
    }
}
