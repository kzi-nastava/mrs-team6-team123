package com.example.mobile_application.repository;

import com.example.mobile_application.dto.TrackRideDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TrackRideService;

import retrofit2.Call;
import retrofit2.Callback;

public class TrackRideRepository {
    private final TrackRideService service;

    public TrackRideRepository() {
        service = ApiClient.getInstance().create(TrackRideService.class);
    }

    public void trackRide(Long rideId, Callback<TrackRideDTO> callback) {
        Call<TrackRideDTO> call = service.trackRide(rideId);
        call.enqueue(callback);
    }
}
