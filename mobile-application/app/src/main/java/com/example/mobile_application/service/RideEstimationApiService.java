
package com.example.mobile_application.service;

import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideEstimationApiService {
    @POST("api/ride-estimation")
    Call<RideEstimationResponseDTO> estimateRide(@Body RideEstimationRequestDTO request);
}
