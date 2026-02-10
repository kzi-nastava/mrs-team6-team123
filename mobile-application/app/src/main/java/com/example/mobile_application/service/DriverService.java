package com.example.mobile_application.service;

import com.example.mobile_application.dto.DriverRegistrationRequestDTO;
import com.example.mobile_application.dto.DriverResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DriverService {

    @POST("api/drivers")
    Call<DriverResponseDTO> registerDriver(@Body DriverRegistrationRequestDTO request);
}
