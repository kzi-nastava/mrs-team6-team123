package com.example.mobile_application.repository;

import com.example.mobile_application.dto.DriverAssignedRideDTO;
import com.example.mobile_application.dto.DriverRegistrationRequestDTO;
import com.example.mobile_application.dto.DriverResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.DriverService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DriverRepository {
    private final DriverService service;

    public DriverRepository() {
        service = ApiClient.getInstance().create(DriverService.class);
    }

    public void registerDriver(DriverRegistrationRequestDTO request, Callback<DriverResponseDTO> callback) {
        Call<DriverResponseDTO> call = service.registerDriver(request);
        call.enqueue(callback);
    }

    public void getAssignedRides(Long driverId, Callback<List<DriverAssignedRideDTO>> callback) {
        Call<List<DriverAssignedRideDTO>> call = service.getAssignedRides(driverId);
        call.enqueue(callback);
    }

    public void startRide(Long driverId, Long rideId, Callback<Void> callback) {
        Call<Void> call = service.startRide(driverId, rideId);
        call.enqueue(callback);
    }
}
