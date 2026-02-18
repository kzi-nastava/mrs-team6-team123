
package com.example.mobile_application.service;

import com.example.mobile_application.dto.PanicAlertRequestDTO;
import com.example.mobile_application.dto.PanicAlertResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PanicService {
    @POST("api/panic")
    Call<PanicAlertResponseDTO> triggerPanic(@Body PanicAlertRequestDTO request);
}