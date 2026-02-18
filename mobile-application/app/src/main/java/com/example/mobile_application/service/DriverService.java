package com.example.mobile_application.service;

import com.example.mobile_application.dto.DriverAssignedRideDTO;
import com.example.mobile_application.dto.DriverRegistrationRequestDTO;
import com.example.mobile_application.dto.DriverResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverService {

    @POST("api/drivers")
    Call<DriverResponseDTO> registerDriver(@Body DriverRegistrationRequestDTO request);

    @GET("api/drivers/{driverId}/assigned-rides")
    Call<List<DriverAssignedRideDTO>> getAssignedRides(@Path("driverId") Long driverId);

    @POST("api/drivers/{driverId}/rides/{rideId}/start")
    Call<Void> startRide(@Path("driverId") Long driverId, @Path("rideId") Long rideId);
}
