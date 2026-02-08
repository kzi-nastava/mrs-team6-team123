package com.example.mobile_application.service;

import com.example.mobile_application.dto.RideMonitoringDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RideMonitoringService {
    @GET("api/rides/monitoring/active")
    Call<List<RideMonitoringDTO>> getAllActiveRides();
}
