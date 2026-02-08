package com.example.mobile_application.repository;

import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideService;

import retrofit2.Call;
import retrofit2.Callback;

public class RideRepository {
    private final RideService rideService;

    public RideRepository() {
        rideService = ApiClient.getInstance().create(RideService.class);
    }

    public void finishRide(Long rideId, Callback<Void> callback) {
        Call<Void> call = rideService.finishRide(rideId);
        call.enqueue(callback);
    }
}
