
package com.example.mobile_application.service;

import com.example.mobile_application.dto.CancelRideRequestDTO;
import com.example.mobile_application.dto.CancelRideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideCancelService {
    @POST("api/rides/{rideId}/cancel")
    Call<CancelRideResponseDTO> cancelRide(
            @Path("rideId") Long rideId,
            @Body CancelRideRequestDTO request);
}