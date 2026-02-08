package com.example.mobile_application.service;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideService {

    @POST("api/rides/{rideId}/finish")
    Call<Void> finishRide(@Path("rideId") Long rideId);
}
