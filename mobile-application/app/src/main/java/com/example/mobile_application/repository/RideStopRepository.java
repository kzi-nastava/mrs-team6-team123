
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.StopRideRequestDTO;
import com.example.mobile_application.dto.StopRideResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideStopService;

import retrofit2.Call;
import retrofit2.Callback;

public class RideStopRepository {
    private final RideStopService service;

    public RideStopRepository() {
        service = ApiClient.getInstance().create(RideStopService.class);
    }

    public void stopRide(Long rideId, StopRideRequestDTO request,
                         Callback<StopRideResponseDTO> callback) {
        Call<StopRideResponseDTO> call = service.stopRide(rideId, request);
        call.enqueue(callback);
    }
}