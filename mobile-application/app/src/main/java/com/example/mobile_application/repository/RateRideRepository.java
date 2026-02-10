package com.example.mobile_application.repository;

import com.example.mobile_application.dto.RateRideRequestDTO;
import com.example.mobile_application.dto.RateRideResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RateRideService;

import retrofit2.Call;
import retrofit2.Callback;

public class RateRideRepository {
    private final RateRideService service;

    public RateRideRepository() {
        this.service = ApiClient.getInstance().create(RateRideService.class);
    }

    public void getRideForRating(Long rideId, Callback<RateRideRequestDTO> callback) {
        Call<RateRideRequestDTO> call = service.getRideForRating(rideId);
        call.enqueue(callback);
    }

    public void rateRide(RateRideResponseDTO dto, Callback<Void> callback) {
        Call<Void> call = service.rateRide(dto);
        call.enqueue(callback);
    }
}
