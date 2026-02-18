package com.example.mobile_application.service;

import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;
import com.example.mobile_application.dto.RideOrderRequestDTO;
import com.example.mobile_application.dto.RideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideOrderService {
    @POST("api/rides/estimate")
    Call<RideEstimationResponseDTO> estimateRide(@Body RideEstimationRequestDTO request);

    @POST("api/rides")
    Call<RideResponseDTO> orderRide(@Body RideOrderRequestDTO request);
}
