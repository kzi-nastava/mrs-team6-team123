package com.example.mobile_application.helper;

import androidx.annotation.NonNull;

import com.example.mobile_application.dto.RateRideRequestDTO;
import com.example.mobile_application.repository.RateRideRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateRideHelper {
    private final RateRideRepository repository;
    private RateRideRequestDTO dto;

    public RateRideHelper() {
        this.repository = new RateRideRepository();
    }

    public RateRideRequestDTO fetchRide(Long rideId) {
        getRideForRating(rideId);
        return dto;
    }

    public void getRideForRating(Long rideId) {
        repository.getRideForRating(rideId, new Callback<RateRideRequestDTO>() {
            @Override
            public void onResponse(
                    @NonNull Call<RateRideRequestDTO> call,
                    @NonNull Response<RateRideRequestDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dto = response.body();
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<RateRideRequestDTO> call,
                    @NonNull Throwable t) {
                return;
            }
        });
    }
}
