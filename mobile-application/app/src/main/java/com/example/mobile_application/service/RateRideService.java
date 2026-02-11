package com.example.mobile_application.service;

import com.example.mobile_application.dto.RateRideRequestDTO;
import com.example.mobile_application.dto.RateRideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RateRideService {

    @GET("api/rides/{rideId}/for-rating")
    Call<RateRideRequestDTO> getRideForRating(@Path("rideId") Long rideId);

    @POST("api/rides/{rideId}/rate")
    Call<Void> rateRide(@Body RateRideResponseDTO dto);
}
