
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideEstimationApiService;

import retrofit2.Call;
import retrofit2.Callback;

public class RideEstimationRepository {
    private final RideEstimationApiService service;

    public RideEstimationRepository() {
        service = ApiClient.getInstance().create(RideEstimationApiService.class);
    }

    public void estimateRide(RideEstimationRequestDTO request,
                             Callback<RideEstimationResponseDTO> callback) {
        Call<RideEstimationResponseDTO> call = service.estimateRide(request);
        call.enqueue(callback);
    }
}