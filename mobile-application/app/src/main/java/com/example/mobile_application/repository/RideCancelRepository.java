
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.CancelRideRequestDTO;
import com.example.mobile_application.dto.CancelRideResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideCancelService;

import retrofit2.Call;
import retrofit2.Callback;

public class RideCancelRepository {
    private final RideCancelService service;

    public RideCancelRepository() {
        service = ApiClient.getInstance().create(RideCancelService.class);
    }

    public void cancelRide(Long rideId, CancelRideRequestDTO request,
                           Callback<CancelRideResponseDTO> callback) {
        Call<CancelRideResponseDTO> call = service.cancelRide(rideId, request);
        call.enqueue(callback);
    }
}