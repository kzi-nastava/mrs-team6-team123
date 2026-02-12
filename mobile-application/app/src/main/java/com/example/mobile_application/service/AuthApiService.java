package com.example.mobile_application.service;

import com.example.mobile_application.dto.auth.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/auth/register")
    Call<RegistrationResponseDTO> register(@Body RegistrationRequestDTO request);

    @POST("api/auth/logout")
    Call<LogoutResponseDTO> logout(@Body LogoutRequestDTO request);

    @GET("api/auth/activate")
    Call<String> activateAccount(@Query("token") String token);

    @POST("api/auth/forgot-password")
    Call<String> forgotPassword(@Body ForgotPasswordRequestDTO request);

    @POST("api/auth/reset-password")
    Call<String> resetPassword(@Body ResetPasswordRequestDTO request);
}