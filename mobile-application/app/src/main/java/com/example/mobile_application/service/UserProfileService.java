package com.example.mobile_application.service;

import com.example.mobile_application.dto.ChangePasswordRequestDTO;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.dto.UserProfileRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserProfileService {

    @GET("api/users/{userId}")
    Call<UserProfileDTO> getProfile(@Path("userId") Long userId);

    @PUT("api/users/{userId}")
    Call<UserProfileDTO> updateProfile(@Path("userId") Long userId, @Body UserProfileRequestDTO request);

    @POST("api/users/{userId}/change-password")
    Call<Void> changePassword(@Path("userId") Long userId, @Body ChangePasswordRequestDTO request);
}
