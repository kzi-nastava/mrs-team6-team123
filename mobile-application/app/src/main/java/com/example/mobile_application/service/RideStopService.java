
package com.example.mobile_application.service;

import com.example.mobile_application.dto.StopRideRequestDTO;
import com.example.mobile_application.dto.StopRideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideStopService {
    @POST("api/rides/{rideId}/stop")
    Call<StopRideResponseDTO> stopRide(
            @Path("rideId") Long rideId,
            @Body StopRideRequestDTO request);
}