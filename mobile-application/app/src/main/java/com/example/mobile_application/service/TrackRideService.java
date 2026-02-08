package com.example.mobile_application.service;

import com.example.mobile_application.dto.TrackRideDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrackRideService {
    @GET("/api/rides/{rideId}/tracking")
    Call<TrackRideDTO> trackRide(@Path("rideId") Long rideId);
}
